/* --- DỮ LIỆU MENU THU NGÂN --- */
const cashierMenuItems = [
    { id: 'tables',    text: 'Quản Lý Bàn', icon: 'fa-border-all',          link: 'cashier.html' },
    { id: 'invoices',  text: 'Hóa Đơn',     icon: 'fa-file-invoice-dollar', link: '#' },
    { id: 'payment',   text: 'Thanh Toán',  icon: 'fa-credit-card',         link: '#' }
];

/* --- HÀM 1: RENDER TOP HEADER (Logo + User) --- */
function renderTopHeader() {
    const headerHtml = `
    <header class="top-header" style="background: white; border-bottom: 1px solid #dfe6e9; padding: 0.8rem 2rem; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 100;">
        <div class="brand" style="display: flex; align-items: center;">
            <div class="logo-box" style="background: #e67e22; color: white; width: 36px; height: 36px; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 1.2rem;">R</div>
            <div style="margin-left: 12px; line-height: 1.2;">
                <h3 style="margin: 0; color: #d35400; font-size: 1rem; font-weight: 700;">Restaurant Manager</h3>
                <span style="font-size: 0.75rem; color: #e67e22; font-weight: 600;">Thu Ngân</span>
            </div>
        </div>
        
        <div class="user-profile" style="display: flex; align-items: center; gap: 12px;">
            <span style="font-weight: 600; color: #2d3436; font-size: 0.9rem;">Lê Thị Thu Ngân</span>
             <img src="https://ui-avatars.com/api/?name=Le+Thi+Thu+Ngan&background=ffeaa7&color=d35400" style="width: 36px; height: 36px; border-radius: 50%; border: 2px solid #fff;">
        </div>
    </header>
    `;

    // Chèn vào đầu body
    document.body.insertAdjacentHTML("afterbegin", headerHtml);
}

/* --- HÀM 2: RENDER MENU TABS (Quản lý bàn, Hóa đơn...) --- */
function renderCashierMenu(activeId) {
    const container = document.getElementById("cashier-menu-container");
    if (!container) return;

    const menuHtml = cashierMenuItems.map(item => {
        // Kiểm tra xem item này có đang active không
        const isActive = item.id === activeId ? 'active' : '';

        // Icon đổi màu nếu active (logic CSS sẽ xử lý, ở đây chỉ render class)
        return `
            <a href="${item.link}" class="cashier-tab-item ${isActive}">
                <i class="fa-solid ${item.icon}"></i>
                <span>${item.text}</span>
            </a>
        `;
    }).join('');

    // Render khung chứa menu
    container.innerHTML = `
        <div class="cashier-tabs-wrapper">
            ${menuHtml}
        </div>
    `;
}