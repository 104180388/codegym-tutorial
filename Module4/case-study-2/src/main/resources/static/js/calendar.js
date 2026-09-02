class GoogleCalendarView {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        if (!this.container) return;

        this.schedules = options.schedules || [];
        this.isAdmin = options.isAdmin || false;
        this.doctors = options.doctors || [];
        this.selectedDoctorId = options.selectedDoctorId || 'all';
        this.onAddShift = options.onAddShift || null;
        this.onDeleteShift = options.onDeleteShift || null;

        this.currentDate = new Date();
        this.init();
    }

    init() {
        this.render();
    }

    setDoctorFilter(doctorId) {
        this.selectedDoctorId = doctorId;
        this.render();
    }

    getStartOfWeek(date) {
        const d = new Date(date);
        const day = d.getDay(); // 0 is Sunday
        const diff = d.getDate() - day; // Adjust to Sunday
        return new Date(d.setDate(diff));
    }

    formatMonthYear(startOfWeek) {
        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6);

        const m1 = startOfWeek.getMonth() + 1;
        const y1 = startOfWeek.getFullYear();
        const m2 = endOfWeek.getMonth() + 1;
        const y2 = endOfWeek.getFullYear();

        if (m1 === m2 && y1 === y2) {
            return `Tháng ${m1}, ${y1}`;
        } else if (y1 === y2) {
            return `Tháng ${m1} – Tháng ${m2}, ${y1}`;
        } else {
            return `Tháng ${m1}/${y1} – Tháng ${m2}/${y2}`;
        }
    }

    render() {
        const startOfWeek = this.getStartOfWeek(this.currentDate);
        const monthYearText = this.formatMonthYear(startOfWeek);

        const days = [];
        for (let i = 0; i < 7; i++) {
            const d = new Date(startOfWeek);
            d.setDate(startOfWeek.getDate() + i);
            days.push(d);
        }

        const dayNames = ['CN', 'THỨ 2', 'THỨ 3', 'THỨ 4', 'THỨ 5', 'THỨ 6', 'THỨ 7'];
        const today = new Date();

        let html = `
            <div class="gcal-wrapper">
                <!-- Google Calendar Toolbar -->
                <div class="gcal-toolbar">
                    <div class="gcal-toolbar-left">
                        <button type="button" class="gcal-btn gcal-btn-today" id="gcalTodayBtn">Hôm nay</button>
                        <div class="gcal-nav-buttons">
                            <button type="button" class="gcal-icon-btn" id="gcalPrevBtn" title="Tuần trước"><i class="fa-solid fa-chevron-left"></i></button>
                            <button type="button" class="gcal-icon-btn" id="gcalNextBtn" title="Tuần sau"><i class="fa-solid fa-chevron-right"></i></button>
                        </div>
                        <h2 class="gcal-title">${monthYearText}</h2>
                    </div>

                    <div class="gcal-toolbar-right">
                        <span class="gcal-badge-view"><i class="fa-solid fa-calendar-week"></i> Tuần</span>
                    </div>
                </div>

                <!-- Calendar View Container -->
                <div class="gcal-calendar-body">
                    <!-- Day Header Row -->
                    <div class="gcal-header-grid">
                        <div class="gcal-header-tz">GMT+07</div>
                        ${days.map((d, index) => {
                            const isToday = this.isSameDay(d, today);
                            return `
                                <div class="gcal-day-header ${isToday ? 'is-today' : ''}">
                                    <div class="gcal-day-name">${dayNames[index]}</div>
                                    <div class="gcal-day-number ${isToday ? 'today-badge' : ''}">${d.getDate()}</div>
                                </div>
                            `;
                        }).join('')}
                    </div>

                    <!-- Time Grid Body -->
                    <div class="gcal-grid-scroll">
                        <div class="gcal-grid">
                            <!-- Time Axis Column -->
                            <div class="gcal-time-axis">
                                <div class="gcal-time-slot">8 AM</div>
                                <div class="gcal-time-slot">9 AM</div>
                                <div class="gcal-time-slot">10 AM</div>
                                <div class="gcal-time-slot">11 AM</div>
                                <div class="gcal-time-slot">12 PM</div>
                                <div class="gcal-time-slot">1 PM</div>
                                <div class="gcal-time-slot">2 PM</div>
                                <div class="gcal-time-slot">3 PM</div>
                                <div class="gcal-time-slot">4 PM</div>
                                <div class="gcal-time-slot">5 PM</div>
                                <div class="gcal-time-slot">6 PM</div>
                            </div>

                            <!-- 7 Day Columns -->
                            ${days.map(d => {
                                const dateStr = this.formatDateISO(d);
                                const daySchedules = this.schedules.filter(s => {
                                    const matchDate = s.workDate === dateStr;
                                    const matchDoctor = !this.selectedDoctorId || this.selectedDoctorId === 'all' || String(s.doctorId) === String(this.selectedDoctorId);
                                    return matchDate && matchDoctor;
                                });
                                const isToday = this.isSameDay(d, today);

                                const morningShifts = daySchedules.filter(s => s.shift === 'MORNING');
                                const afternoonShifts = daySchedules.filter(s => s.shift === 'AFTERNOON');

                                return `
                                    <div class="gcal-day-column ${isToday ? 'is-today-col' : ''}" data-date="${dateStr}">
                                        <!-- Hour Grid Horizontal Lines -->
                                        <div class="gcal-hour-line" style="top: 0px;"></div>
                                        <div class="gcal-hour-line" style="top: 60px;"></div>
                                        <div class="gcal-hour-line" style="top: 120px;"></div>
                                        <div class="gcal-hour-line" style="top: 180px;"></div>
                                        <div class="gcal-hour-line" style="top: 240px;"></div>
                                        <div class="gcal-hour-line" style="top: 300px;"></div>
                                        <div class="gcal-hour-line" style="top: 360px;"></div>
                                        <div class="gcal-hour-line" style="top: 420px;"></div>
                                        <div class="gcal-hour-line" style="top: 480px;"></div>
                                        <div class="gcal-hour-line" style="top: 540px;"></div>

                                        <!-- Shift Event Block: Morning (8:00 AM - 11:30 AM) -->
                                        ${morningShifts.map((shift, idx) => {
                                            const total = morningShifts.length;
                                            const widthPct = 100 / total;
                                            const leftPct = idx * widthPct;
                                            return `
                                                <div class="gcal-event-card shift-morning" style="top: 0px; height: 210px; left: calc(${leftPct}% + 2px); width: calc(${widthPct}% - 4px);" data-id="${shift.id || ''}">
                                                    <div class="gcal-event-title">${shift.doctorName ? shift.doctorName : 'Ca Sáng (Khám bệnh)'}</div>
                                                    <div class="gcal-event-time">8:00 – 11:30 AM</div>
                                                    ${this.isAdmin && shift.id ? `<button type="button" class="gcal-event-del-btn" data-id="${shift.id}" title="Xóa ca trực"><i class="fa-solid fa-xmark"></i></button>` : ''}
                                                </div>
                                            `;
                                        }).join('')}

                                        <!-- Shift Event Block: Afternoon (1:30 PM - 4:30 PM) -->
                                        ${afternoonShifts.map((shift, idx) => {
                                            const total = afternoonShifts.length;
                                            const widthPct = 100 / total;
                                            const leftPct = idx * widthPct;
                                            return `
                                                <div class="gcal-event-card shift-afternoon" style="top: 330px; height: 180px; left: calc(${leftPct}% + 2px); width: calc(${widthPct}% - 4px);" data-id="${shift.id || ''}">
                                                    <div class="gcal-event-title">${shift.doctorName ? shift.doctorName : 'Ca Chiều (Khám bệnh)'}</div>
                                                    <div class="gcal-event-time">1:30 – 4:30 PM</div>
                                                    ${this.isAdmin && shift.id ? `<button type="button" class="gcal-event-del-btn" data-id="${shift.id}" title="Xóa ca trực"><i class="fa-solid fa-xmark"></i></button>` : ''}
                                                </div>
                                            `;
                                        }).join('')}
                                    </div>
                                `;
                            }).join('')}
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.container.innerHTML = html;

        // Attach event listeners
        document.getElementById('gcalTodayBtn').addEventListener('click', () => {
            this.currentDate = new Date();
            this.render();
        });

        document.getElementById('gcalPrevBtn').addEventListener('click', () => {
            this.currentDate.setDate(this.currentDate.getDate() - 7);
            this.render();
        });

        document.getElementById('gcalNextBtn').addEventListener('click', () => {
            this.currentDate.setDate(this.currentDate.getDate() + 7);
            this.render();
        });

        if (this.isAdmin && this.onDeleteShift) {
            this.container.querySelectorAll('.gcal-event-del-btn').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    const id = btn.getAttribute('data-id');
                    if (id && confirm('Bạn có chắc chắn muốn xóa ca trực này khỏi lịch?')) {
                        this.onDeleteShift(id);
                    }
                });
            });
        }
    }

    isSameDay(d1, d2) {
        return d1.getFullYear() === d2.getFullYear() &&
            d1.getMonth() === d2.getMonth() &&
            d1.getDate() === d2.getDate();
    }

    formatDateISO(d) {
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
}
