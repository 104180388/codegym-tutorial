class GoogleCalendarView {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        if (!this.container) return;

        this.schedules = options.schedules || [];
        this.isAdmin = options.isAdmin || false;
        this.doctors = options.doctors || [];
        this.selectedDoctorId = options.selectedDoctorId || 'all';
        this.currentView = options.defaultView || (this.isAdmin ? 'week' : 'month30');

        this.onAddShift = options.onAddShift || null;
        this.onDeleteShift = options.onDeleteShift || null;

        this.currentDate = new Date();
        this.init();
    }

    init() {
        this.updateSummaryStats();
        this.render();
    }

    setDoctorFilter(doctorId) {
        this.selectedDoctorId = doctorId;
        this.updateSummaryStats();
        this.render();
    }

    setView(viewName) {
        this.currentView = viewName;
        this.render();
    }

    updateSummaryStats() {
        // Calculate statistics for current month and remaining active days
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth(); // 0-indexed

        let totalShifts = 0;
        let morningShifts = 0;
        let afternoonShifts = 0;
        const workDaysSet = new Set();

        this.schedules.forEach(s => {
            const matchDoctor = !this.selectedDoctorId || this.selectedDoctorId === 'all' || String(s.doctorId) === String(this.selectedDoctorId);
            if (!matchDoctor) return;

            const [y, m, d] = s.workDate.split('-').map(Number);
            const schedDate = new Date(y, m - 1, d);
            schedDate.setHours(0, 0, 0, 0);

            // Count shifts in the current month from today onwards (or whole month)
            if (schedDate.getFullYear() === currentYear && schedDate.getMonth() === currentMonth) {
                totalShifts++;
                workDaysSet.add(s.workDate);
                if (s.shift === 'MORNING') {
                    morningShifts++;
                } else if (s.shift === 'AFTERNOON') {
                    afternoonShifts++;
                }
            }
        });

        const elTotal = document.getElementById('statTotalShifts');
        const elMorning = document.getElementById('statMorningShifts');
        const elAfternoon = document.getElementById('statAfternoonShifts');
        const elWorkDays = document.getElementById('statWorkDays');

        if (elTotal) elTotal.textContent = `${totalShifts} ca`;
        if (elMorning) elMorning.textContent = `${morningShifts} ca`;
        if (elAfternoon) elAfternoon.textContent = `${afternoonShifts} ca`;
        if (elWorkDays) elWorkDays.textContent = `${workDaysSet.size} ngày làm việc`;
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
        if (this.currentView === 'month30') {
            this.renderMonthView();
        } else {
            this.renderWeek();
        }
    }

    renderToolbar(titleText) {
        return `
            <div class="gcal-toolbar">
                <div class="gcal-toolbar-left">
                    <button type="button" class="gcal-btn gcal-btn-today" id="gcalTodayBtn">Hôm nay</button>
                    <div class="gcal-nav-buttons">
                        <button type="button" class="gcal-icon-btn" id="gcalPrevBtn" title="Trước"><i class="fa-solid fa-chevron-left"></i></button>
                        <button type="button" class="gcal-icon-btn" id="gcalNextBtn" title="Sau"><i class="fa-solid fa-chevron-right"></i></button>
                    </div>
                    <h2 class="gcal-title">${titleText}</h2>
                </div>

                <div class="gcal-toolbar-right">
                    <div class="gcal-view-tabs">
                        <button type="button" class="gcal-tab-btn ${this.currentView === 'month30' ? 'active' : ''}" data-view="month30">
                            <i class="fa-solid fa-calendar-days"></i> Lịch 30 ngày
                        </button>
                        <button type="button" class="gcal-tab-btn ${this.currentView === 'week' ? 'active' : ''}" data-view="week">
                            <i class="fa-solid fa-calendar-week"></i> Theo tuần
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    /* ==========================================================
       VIEW 1: LỊCH 30 NGÀY TRONG THÁNG (MONTH CALENDAR VIEW)
       - Các ngày đã qua trong tháng -> Màu xám
       - Các ngày trong tháng tới -> Màu xám
       - Các ngày hiện tại & tương lai trong tháng -> Màu sắc chuẩn nổi bật
       ========================================================== */
    renderMonthView() {
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const viewYear = this.currentDate.getFullYear();
        const viewMonth = this.currentDate.getMonth(); // 0-indexed

        const titleText = `Tháng ${viewMonth + 1}, ${viewYear}`;

        // First and last day of the currently displayed month
        const firstDayOfMonth = new Date(viewYear, viewMonth, 1);
        const lastDayOfMonth = new Date(viewYear, viewMonth + 1, 0);

        // Find calendar grid start (Sunday of first week) and end (Saturday of last week)
        const startGrid = this.getStartOfWeek(firstDayOfMonth);
        const endGrid = new Date(lastDayOfMonth);
        const endDayOfWeek = endGrid.getDay();
        endGrid.setDate(endGrid.getDate() + (6 - endDayOfWeek));

        const dayNames = ['Chủ Nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];

        // Generate day cells
        const cells = [];
        let curr = new Date(startGrid);
        while (curr <= endGrid) {
            cells.push(new Date(curr));
            curr.setDate(curr.getDate() + 1);
        }

        let html = `
            <div class="gcal-wrapper">
                ${this.renderToolbar(titleText)}

                <div class="gcal-calendar-body">
                    <!-- Day Headers -->
                    <div class="gcal-month-header-grid">
                        ${dayNames.map(name => `<div class="gcal-month-day-head">${name}</div>`).join('')}
                    </div>

                    <!-- Day Grid Cells -->
                    <div class="gcal-month-grid">
                        ${cells.map(d => {
                            const dateStr = this.formatDateISO(d);
                            const cellTime = new Date(d);
                            cellTime.setHours(0, 0, 0, 0);

                            const isToday = this.isSameDay(d, today);
                            const isPast = cellTime < today;
                            const isCurrentMonth = d.getFullYear() === viewYear && d.getMonth() === viewMonth;
                            const isNextMonth = (d.getFullYear() > viewYear) || (d.getFullYear() === viewYear && d.getMonth() > viewMonth);
                            const isPrevMonth = (d.getFullYear() < viewYear) || (d.getFullYear() === viewYear && d.getMonth() < viewMonth);

                            // Determine status: past day or outside month becomes greyed out
                            const isGreyedOut = isPast || !isCurrentMonth;

                            const daySchedules = this.schedules.filter(s => {
                                const matchDate = s.workDate === dateStr;
                                const matchDoctor = !this.selectedDoctorId || this.selectedDoctorId === 'all' || String(s.doctorId) === String(this.selectedDoctorId);
                                return matchDate && matchDoctor;
                            });

                            const morningShift = daySchedules.find(s => s.shift === 'MORNING');
                            const afternoonShift = daySchedules.find(s => s.shift === 'AFTERNOON');
                            const hasShift = morningShift || afternoonShift;

                            let cellClass = 'gcal-month-cell';
                            if (isToday) cellClass += ' is-today';
                            if (isPast && isCurrentMonth) cellClass += ' is-past';
                            if (!isCurrentMonth) cellClass += ' is-other-month';

                            return `
                                <div class="${cellClass}" data-date="${dateStr}">
                                    <div class="gcal-cell-header">
                                        <div class="gcal-cell-number ${isToday ? 'today-pill' : ''}">
                                            ${d.getDate()}
                                        </div>
                                        ${isToday ? '<span class="gcal-cell-tag-today">Hôm nay</span>' : ''}
                                    </div>

                                    <div class="gcal-month-shifts">
                                        ${morningShift ? `
                                            <div class="gcal-month-chip ${isGreyedOut ? 'past-shift' : 'morning'}" title="Ca Sáng: 08:00 - 11:30">
                                                <span><i class="fa-solid fa-sun" style="margin-right:3px;"></i> Sáng (08:00-11:30)</span>
                                                ${this.isAdmin && morningShift.id ? `<button type="button" class="gcal-event-del-btn" data-id="${morningShift.id}" title="Xóa ca"><i class="fa-solid fa-xmark"></i></button>` : ''}
                                            </div>
                                        ` : ''}

                                        ${afternoonShift ? `
                                            <div class="gcal-month-chip ${isGreyedOut ? 'past-shift' : 'afternoon'}" title="Ca Chiều: 13:30 - 16:30">
                                                <span><i class="fa-solid fa-cloud-sun" style="margin-right:3px;"></i> Chiều (13:30-16:30)</span>
                                                ${this.isAdmin && afternoonShift.id ? `<button type="button" class="gcal-event-del-btn" data-id="${afternoonShift.id}" title="Xóa ca"><i class="fa-solid fa-xmark"></i></button>` : ''}
                                            </div>
                                        ` : ''}

                                        ${!hasShift && isCurrentMonth && !isPast ? `
                                            <div class="gcal-month-chip-empty"><i class="fa-solid fa-bed" style="opacity:0.6;"></i> Nghỉ trực</div>
                                        ` : ''}
                                    </div>
                                </div>
                            `;
                        }).join('')}
                    </div>
                </div>
            </div>
        `;

        this.container.innerHTML = html;
        this.attachEventListeners();
    }

    /* ==========================================================
       VIEW 2: CLASSIC GOOGLE CALENDAR WEEK TIME GRID
       ========================================================== */
    renderWeek() {
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
        today.setHours(0, 0, 0, 0);

        let html = `
            <div class="gcal-wrapper">
                ${this.renderToolbar(monthYearText)}

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
                                <div class="gcal-time-slot">08:00</div>
                                <div class="gcal-time-slot">09:00</div>
                                <div class="gcal-time-slot">10:00</div>
                                <div class="gcal-time-slot">11:00</div>
                                <div class="gcal-time-slot">12:00</div>
                                <div class="gcal-time-slot">13:00</div>
                                <div class="gcal-time-slot">14:00</div>
                                <div class="gcal-time-slot">15:00</div>
                                <div class="gcal-time-slot">16:00</div>
                                <div class="gcal-time-slot">17:00</div>
                                <div class="gcal-time-slot">18:00</div>
                            </div>

                            <!-- 7 Day Columns -->
                            ${days.map(d => {
                                const dateStr = this.formatDateISO(d);
                                const cellTime = new Date(d);
                                cellTime.setHours(0, 0, 0, 0);
                                const isPast = cellTime < today;

                                const daySchedules = this.schedules.filter(s => {
                                    const matchDate = s.workDate === dateStr;
                                    const matchDoctor = !this.selectedDoctorId || this.selectedDoctorId === 'all' || String(s.doctorId) === String(this.selectedDoctorId);
                                    return matchDate && matchDoctor;
                                });
                                const isToday = this.isSameDay(d, today);

                                const morningShifts = daySchedules.filter(s => s.shift === 'MORNING');
                                const afternoonShifts = daySchedules.filter(s => s.shift === 'AFTERNOON');

                                return `
                                    <div class="gcal-day-column ${isToday ? 'is-today-col' : ''} ${isPast ? 'is-past-col' : ''}" data-date="${dateStr}">
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

                                        <!-- Shift Event Block: Morning (08:00 - 11:30) -->
                                        ${morningShifts.map((shift, idx) => {
                                            const total = morningShifts.length;
                                            const widthPct = 100 / total;
                                            const leftPct = idx * widthPct;
                                            const shiftClass = isPast ? 'shift-past' : 'shift-morning';
                                            return `
                                                <div class="gcal-event-card ${shiftClass}" style="top: 0px; height: 210px; left: calc(${leftPct}% + 2px); width: calc(${widthPct}% - 4px);" data-id="${shift.id || ''}">
                                                    <div class="gcal-event-title">${shift.doctorName ? shift.doctorName : 'Ca Sáng (Khám bệnh)'}</div>
                                                    <div class="gcal-event-time">08:00 – 11:30</div>
                                                    ${this.isAdmin && shift.id ? `<button type="button" class="gcal-event-del-btn" data-id="${shift.id}" title="Xóa ca trực"><i class="fa-solid fa-xmark"></i></button>` : ''}
                                                </div>
                                            `;
                                        }).join('')}

                                        <!-- Shift Event Block: Afternoon (13:30 - 16:30) -->
                                        ${afternoonShifts.map((shift, idx) => {
                                            const total = afternoonShifts.length;
                                            const widthPct = 100 / total;
                                            const leftPct = idx * widthPct;
                                            const shiftClass = isPast ? 'shift-past' : 'shift-afternoon';
                                            return `
                                                <div class="gcal-event-card ${shiftClass}" style="top: 330px; height: 180px; left: calc(${leftPct}% + 2px); width: calc(${widthPct}% - 4px);" data-id="${shift.id || ''}">
                                                    <div class="gcal-event-title">${shift.doctorName ? shift.doctorName : 'Ca Chiều (Khám bệnh)'}</div>
                                                    <div class="gcal-event-time">13:30 – 16:30</div>
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
        this.attachEventListeners();
    }

    attachEventListeners() {
        // Today button
        const todayBtn = document.getElementById('gcalTodayBtn');
        if (todayBtn) {
            todayBtn.addEventListener('click', () => {
                this.currentDate = new Date();
                this.render();
            });
        }

        // Prev & Next Buttons
        const prevBtn = document.getElementById('gcalPrevBtn');
        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                if (this.currentView === 'month30') {
                    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
                } else {
                    this.currentDate.setDate(this.currentDate.getDate() - 7);
                }
                this.render();
            });
        }

        const nextBtn = document.getElementById('gcalNextBtn');
        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                if (this.currentView === 'month30') {
                    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
                } else {
                    this.currentDate.setDate(this.currentDate.getDate() + 7);
                }
                this.render();
            });
        }

        // View Mode Switcher Tabs
        this.container.querySelectorAll('.gcal-tab-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const view = btn.getAttribute('data-view');
                if (view) {
                    this.setView(view);
                }
            });
        });

        // Admin Delete Shift buttons
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

    formatDateVN(d) {
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    }
}
