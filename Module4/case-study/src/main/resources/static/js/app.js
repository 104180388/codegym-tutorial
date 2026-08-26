/* MedCare jQuery / AJAX Application Script */

$(document).ready(function () {
    console.log("MedCare App JS loaded successfully.");

    // =========================================================================
    // AJAX 1 - Dynamic Time Slots (Tải khung giờ trống)
    // =========================================================================
    function loadAvailableTimeSlots() {
        const doctorId = $('#doctorIdSelect').val();
        const dateVal = $('#appointmentDateInput').val();
        const $timeSlotContainer = $('#timeSlotContainer');
        const $timeSlotSelect = $('#appointmentTimeSelect');

        if (!doctorId || !dateVal) {
            return;
        }

        $timeSlotContainer.html('<div class="text-teal small"><i class="fa-solid fa-spinner fa-spin me-1"></i>Đang tải khung giờ còn trống...</div>');

        $.ajax({
            url: '/api/schedules/available',
            type: 'GET',
            data: { doctorId: doctorId, date: dateVal },
            dataType: 'json',
            success: function (schedules) {
                if (!schedules || schedules.length === 0) {
                    $timeSlotContainer.html('<div class="alert alert-warning small py-2 mb-0"><i class="fa-solid fa-circle-exclamation me-1"></i>Không có ca khám trống cho bác sĩ vào ngày này. Vui lòng chọn ngày khác.</div>');
                    $timeSlotSelect.empty().append('<option value="">-- Không có ca khám --</option>');
                } else {
                    let html = '<div class="d-flex flex-wrap gap-2 mt-2">';
                    $timeSlotSelect.empty().append('<option value="">-- Chọn khung giờ --</option>');

                    $.each(schedules, function (index, sch) {
                        html += `
                            <label class="btn btn-outline-teal btn-sm rounded-pill slot-badge">
                                <input type="radio" name="selectedSlotRadio" value="${sch.timeSlot}" data-schedule-id="${sch.id}" class="d-none">
                                <i class="fa-regular fa-clock me-1"></i>${sch.timeSlot} <span class="badge bg-teal bg-opacity-20 text-teal ms-1">Còn ${sch.availableSlots} chỗ</span>
                            </label>
                        `;
                        $timeSlotSelect.append(`<option value="${sch.timeSlot}" data-schedule-id="${sch.id}">${sch.timeSlot} (Còn ${sch.availableSlots} chỗ)</option>`);
                    });
                    html += '</div>';

                    $timeSlotContainer.html(html);
                }
            },
            error: function (xhr, status, error) {
                console.error("Lỗi AJAX 1 (Dynamic Time Slots):", error);
                $timeSlotContainer.html('<div class="text-muted small">Chọn ca khám theo mặc định bên dưới</div>');
            }
        });
    }

    // Lắng nghe sự kiện change trên ô Bác sĩ & Ngày khám
    $(document).on('change', '#doctorIdSelect, #appointmentDateInput', function () {
        loadAvailableTimeSlots();
        calculateRealtimeTotal();
    });

    // Lắng nghe sự kiện click trên ô Radio button slot động
    $(document).on('click', '.slot-badge', function () {
        $('.slot-badge').removeClass('active btn-teal text-white').addClass('btn-outline-teal');
        $(this).removeClass('btn-outline-teal').addClass('active btn-teal text-white');
        
        const slotVal = $(this).find('input[name="selectedSlotRadio"]').val();
        const schedId = $(this).find('input[name="selectedSlotRadio"]').data('schedule-id');
        
        $('#appointmentTimeSelect').val(slotVal);
        $('#scheduleIdInput').val(schedId);
    });

    // =========================================================================
    // AJAX 2 - Pre-service Dynamic Calculator (Tính tổng tiền dự kiến)
    // =========================================================================
    function calculateRealtimeTotal() {
        let total = 0;

        // 1. Tiền khám bác sĩ
        const $selectedDoctorOpt = $('#doctorIdSelect option:selected');
        const docFee = parseFloat($selectedDoctorOpt.data('fee')) || 0;
        total += docFee;

        // 2. Tiền các dịch vụ cận lâm sàng tích chọn
        $('.service-checkbox:checked').each(function () {
            const srvPrice = parseFloat($(this).data('price')) || 0;
            total += srvPrice;
        });

        // 3. Định dạng VND hiển thị real-time
        const formattedTotal = new Intl.NumberFormat('vi-VN').format(total) + ' VNĐ';
        $('#realtimeTotalDisplay').text(formattedTotal);
        $('#docFeeDisplay').text(new Intl.NumberFormat('vi-VN').format(docFee) + ' VNĐ');
    }

    // Lắng nghe sự kiện tick/bỏ tick checkbox dịch vụ
    $(document).on('change', '.service-checkbox', function () {
        calculateRealtimeTotal();
    });

    // Chạy tính toán ban đầu khi trang vừa tải
    calculateRealtimeTotal();

    // =========================================================================
    // AJAX 3 - Feedback / Review (Gửi đánh giá bất đồng bộ)
    // =========================================================================
    $(document).on('submit', '#ajaxReviewForm', function (e) {
        e.preventDefault();

        const doctorId = $('#reviewDoctorId').val();
        const patientName = $('#reviewPatientName').val();
        const rating = parseInt($('#reviewRating').val()) || 5;
        const comment = $('#reviewComment').val();
        const $btnSubmit = $('#btnSubmitReview');

        if (!comment || comment.trim() === '') {
            alert('Vui lòng nhập nội dung nhận xét/đánh giá!');
            return;
        }

        $btnSubmit.prop('disabled', true).html('<i class="fa-solid fa-spinner fa-spin me-1"></i>Đang gửi...');

        const reviewData = {
            doctorId: doctorId ? parseInt(doctorId) : null,
            patientName: patientName,
            rating: rating,
            comment: comment
        };

        $.ajax({
            url: '/api/reviews',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(reviewData),
            success: function (newReview) {
                $btnSubmit.prop('disabled', false).html('<i class="fa-solid fa-paper-plane me-1"></i>Gửi Đánh Giá');

                // Render thẻ bình luận mới trực tiếp không F5
                let starsHtml = '';
                for (let i = 1; i <= 5; i++) {
                    if (i <= newReview.rating) {
                        starsHtml += '<i class="fa-solid fa-star text-warning me-1"></i>';
                    } else {
                        starsHtml += '<i class="fa-regular fa-star text-muted me-1"></i>';
                    }
                }

                const newCardHtml = `
                    <div class="card-medcare p-3 mb-3 border-start border-3 border-teal fade-in-card">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <div class="d-flex align-items-center gap-2">
                                <div class="w-8 h-8 rounded-circle bg-teal text-white d-flex align-items-center justify-content-center fw-bold small" style="width:32px; height:32px; background-color:#0d9488;">
                                    ${(newReview.patientName || 'BN').charAt(0).toUpperCase()}
                                </div>
                                <div>
                                    <strong class="d-block text-dark small">${newReview.patientName || 'Bệnh nhân'}</strong>
                                    <div class="small">${starsHtml}</div>
                                </div>
                            </div>
                            <span class="badge bg-light text-muted border small">Vừa xong</span>
                        </div>
                        <p class="text-secondary small mb-0">${newReview.comment}</p>
                    </div>
                `;

                $('#reviewListContainer').prepend(newCardHtml);
                $('#reviewComment').val('');
                $('#reviewAlertSuccess').removeClass('d-none').hide().fadeIn().delay(3000).fadeOut();
            },
            error: function (xhr, status, error) {
                console.error("Lỗi AJAX 3 (Review):", error);
                $btnSubmit.prop('disabled', false).html('<i class="fa-solid fa-paper-plane me-1"></i>Gửi Đánh Giá');
                alert('Không thể gửi đánh giá: ' + (xhr.responseText || error));
            }
        });
    });
});
