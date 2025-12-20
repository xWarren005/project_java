/* --- header.js --- */

// 1. DATA CẤU HÌNH MENU
const menuItems = [
    { id: 'overview', text: 'Tổng Quan', icon: 'fa-border-all', link: 'overview.html' },
    { id: 'dishes',   text: 'Món Ăn',    icon: 'fa-utensils',  link: 'dishes.html' },
    { id: 'tables',   text: 'Bàn',       icon: 'fa-chair',     link: 'tables.html' },
    { id: 'revenue',  text: 'Doanh Thu', icon: 'fa-chart-line',link: '#' },
    { id: 'qr',       text: 'QR Code',   icon: 'fa-qrcode',    link: '#' }
];

// 2. TỰ ĐỘNG CHÈN TOP HEADER
document.addEventListener("DOMContentLoaded", () => {
    // HTML Template cho Header
    const headerHTML = `
        <header class="main-header">
            <div class="brand-group">
                <div class="logo-box">R</div>
                <div class="text-group">
                    <h1 class="app-name">Restaurant Manager</h1>
                    <span class="role-label">Quản Lý</span>
                </div>
            </div>
            <div class="user-group">
                <img src="https://ui-avatars.com/api/?name=Quan+Ly&background=2E25D1&color=fff" class="avatar">
                <span class="username">Nguyễn Văn Quản Lý</span>
            </div>
        </header>
    `;

    // Chèn vào đầu thẻ body
    document.body.insertAdjacentHTML("afterbegin", headerHTML);
});

// 3. HÀM RENDER MENU (Gọi hàm này ở từng trang riêng lẻ)
function renderMenu(activeId) {
    const container = document.getElementById("menu-container");
    if (!container) return;

    const html = menuItems.map(item => {
        const isActive = item.id === activeId ? 'active' : '';
        return `
            <a href="${item.link}" class="tab-item ${isActive}">
                <i class="fa-solid ${item.icon}"></i> ${item.text}
            </a>
        `;
    }).join('');

    container.innerHTML = `<nav class="nav-tabs">${html}</nav>`;
}