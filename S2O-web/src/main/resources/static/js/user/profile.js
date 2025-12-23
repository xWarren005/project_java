// --- 1. MOCK DATA (Dữ liệu giả lập - Tiếng Việt có dấu) ---
const mockData = {
    user: {
        id: 101,
        fullname: "Nguyễn Văn A", // Có dấu
        email: "nguyenvana@email.com",
        avatar: "https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/07/anh-mat-cuoi-2.jpg",
        rank: "Thành viên Bậc Vàng" // Có dấu
    },
    menu: [
        { id: "menu_cal", icon: "far fa-calendar-alt", label: "Lịch của tôi", action: "calendar" },
        { id: "menu_vou", icon: "fas fa-gift", label: "Voucher của tôi", action: "voucher" }
    ],
    calendar: [
        {
            id: "cal_01",
            place: "Nhà hàng Bamboo Garden", // Có dấu
            date: "25/12/2025",
            time: "19:00",
            status: "upcoming",
            statusText: "Sắp diễn ra" // Có dấu
        },
        {
            id: "cal_02",
            place: "Nhà hàng Sunset View",
            date: "20/12/2025",
            time: "12:30",
            status: "finished",
            statusText: "Đã hoàn thành" // Có dấu
        }
    ],
    vouchers: [
        {
            id: "vou_01",
            title: "Giảm 20%",
            desc: "Áp dụng cho đơn hàng từ 500.000đ", // Có dấu
            expiry: "31/12/2025",
            code: "GIAM20VIP"
        },
        {
            id: "vou_02",
            title: "Miễn phí giao hàng",
            desc: "Cho đơn hàng bất kỳ",
            expiry: "28/12/2025",
            code: "FREESHIP"
        }
    ],
    actions: [
        { id: "act_update", icon: "fas fa-cog", label: "Cập nhật thông tin" },
        { id: "act_logout", icon: "fas fa-sign-out-alt", label: "Đăng xuất" }
    ]
};

// --- 2. HÀM RENDER UI (Xây dựng giao diện từ Data) ---

const App = {
    // Âm thanh
    sound: document.getElementById('click-sound'),

    playSound: function() {
        if(this.sound) {
            this.sound.currentTime = 0;
            this.sound.play().catch(e => console.log("Cần tương tác người dùng để phát âm thanh"));
        }
    },

    init: function() {
        this.renderUser();
        this.renderMenu();
        this.renderCalendar();
        this.renderVouchers();
        this.renderFooter();
    },

    renderUser: function() {
        const frame = document.getElementById('user-info-frame');
        const u = mockData.user;
        frame.innerHTML = `
            <div class="avatar-frame" onclick="App.playSound()">
                <img src="${u.avatar}" alt="Avatar">
            </div>
            <div class="user-details">
                <h2 class="user-name"><span id="u-name">${u.fullname}</span></h2>
                <span class="user-email">${u.email}</span>
                <div class="rank-badge">
                    <i class="fas fa-star"></i>
                    <span>${u.rank}</span>
                </div>
            </div>
        `;
    },

    renderMenu: function() {
        const frame = document.getElementById('quick-menu-frame');
        frame.innerHTML = mockData.menu.map(item => `
            <div class="menu-item" onclick="App.handleMenuClick('${item.action}')">
                <i class="${item.icon}"></i>
                <span>${item.label}</span>
            </div>
        `).join('');
    },

    renderCalendar: function() {
        const frame = document.getElementById('calendar-list-frame');
        frame.innerHTML = mockData.calendar.map(item => `
            <div class="list-card">
                <div class="card-top">
                    <h4 class="card-name">${item.place}</h4>
                    <span class="status-badge status-${item.status}">${item.statusText}</span>
                </div>
                <div class="card-meta">
                    <span><i class="far fa-calendar"></i> <span id="date-${item.id}">${item.date}</span></span>
                    <span><i class="far fa-clock"></i> <span>${item.time}</span></span>
                </div>
            </div>
        `).join('');
    },

    renderVouchers: function() {
        const frame = document.getElementById('voucher-list-frame');
        // Render cấu trúc 2 mặt (Front/Back) để làm hiệu ứng lật
        frame.innerHTML = mockData.vouchers.map(v => `
            <div class="voucher-container" onclick="App.flipCard(this)">
                <div class="voucher-inner">
                    <div class="voucher-front">
                        <div class="v-content">
                            <h4 class="v-title">${v.title}</h4>
                            <p class="v-desc">${v.desc}</p>
                            <p class="v-date">HSD: <span>${v.expiry}</span></p>
                        </div>
                        <div class="v-icon">
                            <i class="fas fa-gift"></i>
                        </div>
                    </div>
                    <div class="voucher-back">
                        <p style="font-size: 0.8rem; opacity: 0.8">MÃ CODE CỦA BẠN:</p> <h3 style="font-size: 1.5rem; font-weight: bold">${v.code}</h3>
                    </div>
                </div>
            </div>
        `).join('');
    },

    renderFooter: function() {
        const frame = document.getElementById('footer-frame');
        frame.innerHTML = mockData.actions.map(act => `
            <div class="action-btn" onclick="App.handleMenuClick('${act.id}')">
                <div class="btn-left">
                    <div class="icon-circle"><i class="${act.icon}"></i></div>
                    <span>${act.label}</span>
                </div>
                <i class="fas fa-chevron-right" style="color: #ccc; font-size: 0.8rem"></i>
            </div>
        `).join('');
    },

    // --- 3. ACTIONS (Xử lý sự kiện) ---

    handleMenuClick: function(action) {
        this.playSound();
        console.log("Người dùng click vào:", action);

        // Demo xử lý sự kiện
        if(action === 'act_logout') {
            const confirmLogout = confirm("Bạn có chắc chắn muốn đăng xuất không?");
            if(confirmLogout) {
                alert("Đã gửi yêu cầu đăng xuất!");
            }
        }
    },

    flipCard: function(element) {
        this.playSound();
        // Tìm thẻ con .voucher-inner và toggle class xoay
        const inner = element.querySelector('.voucher-inner');
        inner.classList.toggle('is-flipped');
    }
};

// Chạy ứng dụng khi tải xong
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});