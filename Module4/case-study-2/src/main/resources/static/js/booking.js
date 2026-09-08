document.addEventListener('DOMContentLoaded', function () {
    const doctorSelect = document.getElementById('doctorId');
    const dateInput = document.getElementById('appointmentDate');
    const slotContainer = document.getElementById('slotContainer');
    const selectedTimeInput = document.getElementById('appointmentTime');
    const serviceSelect = document.getElementById('serviceId');
    const prepGuideBox = document.getElementById('prepGuideBox');
    const prepGuideText = document.getElementById('prepGuideText');

    let initialDoctorId = doctorSelect ? doctorSelect.value : null;
    let initialTime = selectedTimeInput ? selectedTimeInput.value : null;

    function updatePrepGuide() {
        if (!serviceSelect || !prepGuideBox || !prepGuideText) return;
        let guide = '';
        if (serviceSelect.tagName === 'SELECT' && serviceSelect.selectedIndex >= 0) {
            const selectedOption = serviceSelect.options[serviceSelect.selectedIndex];
            guide = selectedOption ? selectedOption.getAttribute('data-prep') : '';
        } else if (serviceSelect.getAttribute('data-prep')) {
            guide = serviceSelect.getAttribute('data-prep');
        }

        if (guide && guide.trim() !== '') {
            prepGuideText.textContent = guide;
            prepGuideBox.style.display = 'block';
        } else if (serviceSelect.tagName === 'SELECT') {
            prepGuideBox.style.display = 'none';
        }
    }

    function fetchAvailableDoctors(timeStr, preselectedDoctorId) {
        if (!doctorSelect) return;

        const serviceId = serviceSelect ? serviceSelect.value : '';
        const dateStr = dateInput ? dateInput.value : '';

        if (!serviceId || !dateStr || !timeStr) {
            doctorSelect.innerHTML = '<option value="">-- Vui lòng chọn Khung giờ khám trước --</option>';
            return;
        }

        const targetDoctorId = preselectedDoctorId || doctorSelect.value;
        doctorSelect.innerHTML = '<option value="">-- Đang tải danh sách bác sĩ khả dụng... --</option>';

        fetch(`/api/appointments/available-doctors?serviceId=${encodeURIComponent(serviceId)}&date=${encodeURIComponent(dateStr)}&time=${encodeURIComponent(timeStr)}`)
            .then(res => res.json())
            .then(doctors => {
                if (!doctors || doctors.length === 0) {
                    doctorSelect.innerHTML = '<option value="">-- Không có bác sĩ nào khả dụng trong khung giờ này --</option>';
                    return;
                }

                doctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ phụ trách --</option>';
                let matched = false;

                doctors.forEach(doc => {
                    const opt = document.createElement('option');
                    opt.value = doc.id;
                    const expText = doc.experienceYears ? ` - ${doc.experienceYears} năm KN` : '';
                    opt.textContent = `BS. ${doc.fullName} (${doc.degree || 'Bác sĩ chuyên khoa'}${expText})`;

                    if (targetDoctorId && String(targetDoctorId) === String(doc.id)) {
                        opt.selected = true;
                        matched = true;
                    }
                    doctorSelect.appendChild(opt);
                });

                // Auto-select if only 1 doctor is available and no match was explicitly selected
                if (!matched && doctors.length === 1) {
                    doctorSelect.selectedIndex = 1;
                }
            })
            .catch(err => {
                console.error('Lỗi khi tải danh sách bác sĩ khả dụng:', err);
                doctorSelect.innerHTML = '<option value="">-- Lỗi tải danh sách bác sĩ --</option>';
            });
    }

    function fetchTimeSlots(maintainSelection = false) {
        if (!slotContainer) return;

        const serviceId = serviceSelect ? serviceSelect.value : '';
        const dateStr = dateInput ? dateInput.value : '';

        if (!serviceId || !dateStr) {
            slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;">Vui lòng chọn Dịch vụ y tế và Ngày khám ở trên để hiển thị các khung giờ khả dụng.</p>';
            if (doctorSelect) {
                doctorSelect.innerHTML = '<option value="">-- Vui lòng chọn Khung giờ khám trước --</option>';
            }
            if (selectedTimeInput) {
                selectedTimeInput.value = '';
            }
            return;
        }

        slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;"><i class="fa-solid fa-spinner fa-spin"></i> Đang tải các khung giờ khả dụng...</p>';

        if (!maintainSelection) {
            if (selectedTimeInput) {
                selectedTimeInput.value = '';
            }
            if (doctorSelect) {
                doctorSelect.innerHTML = '<option value="">-- Vui lòng chọn Khung giờ khám trước --</option>';
            }
        }

        fetch(`/api/appointments/available-slots?serviceId=${encodeURIComponent(serviceId)}&date=${encodeURIComponent(dateStr)}`)
            .then(res => res.json())
            .then(slots => {
                slotContainer.innerHTML = '';
                if (!slots || slots.length === 0) {
                    slotContainer.innerHTML = '<p style="color:var(--danger); font-size:0.9rem;">Không tìm thấy khung giờ khám nào cho ngày đã chọn.</p>';
                    return;
                }

                const grid = document.createElement('div');
                grid.className = 'slot-grid';

                let selectedSlotFound = false;

                slots.forEach(slot => {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.className = `slot-btn ${slot.available ? 'available' : 'disabled'}`;
                    btn.textContent = slot.formattedTime;
                    btn.disabled = !slot.available;

                    const currentTimeVal = selectedTimeInput ? selectedTimeInput.value : '';
                    if (maintainSelection && currentTimeVal && currentTimeVal.startsWith(slot.timeStr) && slot.available) {
                        btn.classList.add('selected');
                        selectedSlotFound = true;
                    }

                    if (slot.available) {
                        btn.addEventListener('click', function () {
                            document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
                            btn.classList.add('selected');
                            if (selectedTimeInput) {
                                selectedTimeInput.value = slot.timeStr;
                            }
                            fetchAvailableDoctors(slot.timeStr);
                        });
                    }

                    grid.appendChild(btn);
                });

                slotContainer.appendChild(grid);

                if (maintainSelection && selectedSlotFound && selectedTimeInput && selectedTimeInput.value) {
                    fetchAvailableDoctors(selectedTimeInput.value, initialDoctorId);
                } else if (!maintainSelection) {
                    if (doctorSelect) {
                        doctorSelect.innerHTML = '<option value="">-- Vui lòng chọn Khung giờ khám trước --</option>';
                    }
                }
            })
            .catch(err => {
                console.error('Lỗi tải time slots:', err);
                slotContainer.innerHTML = '<p style="color:var(--danger); font-size:0.9rem;">Lỗi kết nối khi tải danh sách khung giờ.</p>';
            });
    }

    // Event listeners
    if (serviceSelect && serviceSelect.tagName === 'SELECT') {
        serviceSelect.addEventListener('change', function () {
            updatePrepGuide();
            initialDoctorId = null;
            initialTime = null;
            fetchTimeSlots(false);
        });
    }

    if (dateInput) {
        dateInput.addEventListener('change', function () {
            initialDoctorId = null;
            initialTime = null;
            fetchTimeSlots(false);
        });
    }

    // Initialize state on page load
    updatePrepGuide();
    if (serviceSelect && serviceSelect.value && dateInput && dateInput.value) {
        fetchTimeSlots(Boolean(initialTime));
    }
});
