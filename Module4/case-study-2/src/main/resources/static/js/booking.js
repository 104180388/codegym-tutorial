document.addEventListener('DOMContentLoaded', function () {
    const doctorSelect = document.getElementById('doctorId');
    const dateInput = document.getElementById('appointmentDate');
    const slotContainer = document.getElementById('slotContainer');
    const selectedTimeInput = document.getElementById('appointmentTime');
    const serviceSelect = document.getElementById('serviceId');
    const prepGuideBox = document.getElementById('prepGuideBox');
    const prepGuideText = document.getElementById('prepGuideText');

    function fetchDoctorsByService(serviceId, preselectedDoctorId) {
        if (!doctorSelect) return;

        if (!serviceId) {
            doctorSelect.innerHTML = '<option value="">-- Vui lòng chọn dịch vụ trước --</option>';
            if (slotContainer) {
                slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;">Vui lòng chọn Dịch vụ và Bác sĩ để xem các khung giờ còn trống.</p>';
            }
            return;
        }

        const initialDoctorId = preselectedDoctorId || (doctorSelect ? doctorSelect.value : null);
        doctorSelect.innerHTML = '<option value="">-- Đang tải danh sách bác sĩ chuyên khoa... --</option>';

        fetch(`/api/appointments/doctors-by-service?serviceId=${serviceId}`)
            .then(res => res.json())
            .then(doctors => {
                doctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
                if (!doctors || doctors.length === 0) {
                    doctorSelect.innerHTML = '<option value="">-- Chưa có bác sĩ phụ trách dịch vụ này --</option>';
                    if (slotContainer) {
                        slotContainer.innerHTML = '<p style="color:var(--danger); font-size:0.9rem;">Chưa có bác sĩ được phân công cho dịch vụ này.</p>';
                    }
                    return;
                }
                doctors.forEach(doc => {
                    const opt = document.createElement('option');
                    opt.value = doc.id;
                    opt.textContent = `${doc.fullName} (${doc.degree || 'Bác sĩ chuyên khoa'})`;
                    if (initialDoctorId && String(initialDoctorId) === String(doc.id)) {
                        opt.selected = true;
                    }
                    doctorSelect.appendChild(opt);
                });

                if (doctorSelect.value) {
                    fetchTimeSlots();
                } else if (doctors.length === 1) {
                    doctorSelect.value = doctors[0].id;
                    fetchTimeSlots();
                } else {
                    if (slotContainer) {
                        slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;">Vui lòng chọn Bác sĩ và Ngày khám để xem các khung giờ còn trống.</p>';
                    }
                }
            })
            .catch(err => {
                console.error('Lỗi tải danh sách bác sĩ:', err);
                doctorSelect.innerHTML = '<option value="">-- Lỗi tải danh sách bác sĩ --</option>';
            });
    }

    function fetchTimeSlots() {
        if (!doctorSelect || !dateInput || !slotContainer) return;

        const doctorId = doctorSelect.value;
        const dateStr = dateInput.value;

        if (!doctorId || !dateStr) {
            slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;">Vui lòng chọn Bác sĩ và Ngày khám để xem các khung giờ còn trống.</p>';
            return;
        }

        slotContainer.innerHTML = '<p class="text-slate" style="font-size:0.9rem;">Đang tải khung giờ khả dụng...</p>';

        fetch(`/api/appointments/available-slots?doctorId=${doctorId}&date=${dateStr}`)
            .then(response => response.json())
            .then(slots => {
                slotContainer.innerHTML = '';
                if (!slots || slots.length === 0) {
                    slotContainer.innerHTML = '<p style="color: var(--danger); font-size: 0.9rem;">Không tìm thấy khung giờ khám cho ngày đã chọn.</p>';
                    return;
                }

                const grid = document.createElement('div');
                grid.className = 'slot-grid';

                slots.forEach(slot => {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.className = `slot-btn ${slot.available ? 'available' : 'disabled'}`;
                    btn.textContent = slot.formattedTime;
                    btn.disabled = !slot.available;

                    if (selectedTimeInput && selectedTimeInput.value === slot.timeStr) {
                        btn.classList.add('selected');
                    }

                    if (slot.available) {
                        btn.addEventListener('click', function () {
                            document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
                            btn.classList.add('selected');
                            if (selectedTimeInput) {
                                selectedTimeInput.value = slot.timeStr;
                            }
                        });
                    }

                    grid.appendChild(btn);
                });

                slotContainer.appendChild(grid);
            })
            .catch(err => {
                console.error('Lỗi khi tải time slots:', err);
                slotContainer.innerHTML = '<p style="color: var(--danger); font-size: 0.9rem;">Lỗi kết nối khi tải khung giờ.</p>';
            });
    }

    if (serviceSelect) {
        serviceSelect.addEventListener('change', function () {
            const selectedOption = serviceSelect.options[serviceSelect.selectedIndex];
            const serviceId = serviceSelect.value;
            const guide = selectedOption ? selectedOption.getAttribute('data-prep') : '';

            if (prepGuideBox && prepGuideText) {
                if (guide && guide.trim() !== '') {
                    prepGuideText.textContent = guide;
                    prepGuideBox.style.display = 'block';
                } else {
                    prepGuideBox.style.display = 'none';
                }
            }

            fetchDoctorsByService(serviceId);
        });

        // Trigger on load if service is pre-selected
        if (serviceSelect.value) {
            const selectedOption = serviceSelect.options[serviceSelect.selectedIndex];
            const guide = selectedOption ? selectedOption.getAttribute('data-prep') : '';
            if (prepGuideBox && prepGuideText) {
                if (guide && guide.trim() !== '') {
                    prepGuideText.textContent = guide;
                    prepGuideBox.style.display = 'block';
                } else {
                    prepGuideBox.style.display = 'none';
                }
            }
            const preDoctorId = doctorSelect ? doctorSelect.value : null;
            fetchDoctorsByService(serviceSelect.value, preDoctorId);
        }
    }

    if (doctorSelect) doctorSelect.addEventListener('change', fetchTimeSlots);
    if (dateInput) dateInput.addEventListener('change', fetchTimeSlots);

    // Initial fetch if doctor and date are pre-selected
    if (doctorSelect && dateInput && doctorSelect.value && dateInput.value) {
        fetchTimeSlots();
    }
});
