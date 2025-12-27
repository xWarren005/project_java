const staticUI = {
    menu: [
        { id: "menu_cal", icon: "far fa-calendar-alt", label: "Lịch của tôi", action: "history" },
        { id: "menu_vou", icon: "fas fa-gift", label: "Voucher của tôi", action: "voucher" }
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
        this.renderFooter();
        this.renderMenu();
        this.fetchProfileData();
    },
// --- GỌI API BACKEND ---
    fetchProfileData: function() {

        fetch('/api/user/profile-data')
            .then(res => {
                if (res.status === 401) {
                    // Nếu chưa đăng nhập hoặc hết phiên
                    window.location.href = '/user/login';
                    return null;
                }
                return res.json();
            })
            .then(data => {
                if (data) {
                    // Đổ dữ liệu thật vào các hàm render
                    this.renderUser(data.user);
                    this.renderCalendar(data.calendar);
                    this.renderVouchers(data.vouchers);
                }
            })
            .catch(err => {
                console.error("Lỗi tải profile:", err);
                // Có thể hiển thị thông báo lỗi nhẹ ở đây nếu muốn
            });
    },
    // --- CÁC HÀM RENDER
    renderUser: function(u) {
        const frame = document.getElementById('user-info-frame');
        // Fallback nếu ảnh lỗi
        const avatarUrl = u.avatar || '/images/default-avatar.png';
        frame.innerHTML = `
            <div class="avatar-frame" onclick="App.playSound()">
                <img src="${avatarUrl}" alt="Avatar" onerror="this.src='/images/default-avatar.png'">
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
        // Dùng staticUI vì đây là menu cố định
        frame.innerHTML = staticUI.menu.map(item => `
            <div class="menu-item" onclick="App.handleMenuClick('${item.action}')">
                <i class="${item.icon}"></i>
                <span>${item.label}</span>
            </div>
        `).join('');
    },

    renderCalendar: function(calendarList) {
        const frame = document.getElementById('calendar-list-frame');
        if (!calendarList || calendarList.length === 0) {
            frame.innerHTML = '<p style="text-align:center; color:#999; padding:20px">Chưa có lịch sử đơn hàng</p>';
            return;
        }
        frame.innerHTML = calendarList.map(item => `
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

    renderVouchers: function(voucherList) {
        const frame = document.getElementById('voucher-list-frame');
        if (!voucherList || voucherList.length === 0) {
            frame.innerHTML = '<p style="text-align:center; color:#999; padding:10px">Bạn chưa có voucher nào</p>';
            return;
        }
        // Render cấu trúc 2 mặt (Front/Back) để làm hiệu ứng lật
        frame.innerHTML = voucherList.map(v => `
            <div class="voucher-container" onclick="App.flipCard(this)">
                <div class="voucher-inner">
                    <div class="voucher-front">
                        <div class="v-content">
                            <h4 class="v-title">${v.title}</h4>
                            <p class="v-desc">${v.description || v.desc || ''}</p>
                            <p class="v-date">HSD: <span>${v.expiry || 'Vô thời hạn'}</span></p>
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
        // Dùng staticUI
        frame.innerHTML = staticUI.actions.map(act => `
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
                window.location.href = '/user/logout';
            }
        }
        else if (action === 'calendar') {
            // Scroll đến phần lịch (nếu cần)
            document.getElementById('calendar-list-frame').scrollIntoView({ behavior: 'smooth' });
        }
        else if (action === 'act_update') {
            alert("Tính năng cập nhật thông tin đang phát triển!");
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