/*   1. MOCK DATA (Mô phỏng dữ liệu Server) */
const managerData = {
    user: "Nguyễn Văn Quản Lý",
    stats: {
        totalTables: { value: 12, note: "4 bàn đang có khách" },
        dishes: { value: 24, note: "22 món đang phục vụ" },
        orders: { value: 8, note: "3 đơn đang chế biến" },
        revenueToday: { value: "5.2tr", note: "+12% so với hôm qua" }
    },
    revenueDetail: {
        total: "5.2tr VNĐ",
        growth: 11.8,
        ordersCount: 2,
        invoicesCount: 0
    },
    // Trạng thái: 0=Trống, 1=Có khách, 2=Đã đặt
    tables: [
        { id: 1, status: 0, name: "Trống" },
        { id: 2, status: 1, name: "Có Khách" },
        { id: 3, status: 2, name: "Đã Đặt" },
        { id: 4, status: 0, name: "Trống" },
        { id: 5, status: 1, name: "Có Khách" },
        { id: 6, status: 2, name: "Đã Đặt" },
        { id: 7, status: 0, name: "Trống" },
        { id: 8, status: 1, name: "Có Khách" }
    ]
};

/*2. MAIN LOGIC*/
document.addEventListener("DOMContentLoaded", () => {
    initDashboard();
    setupTabs();
});

function initDashboard() {
    // 1. Set User Info (Avatar màu xanh 2E25D1)
    document.getElementById("user-name").textContent = managerData.user;
    document.getElementById("user-avatar").src =
        "https://ui-avatars.com/api/?name=Quan+Ly&background=2E25D1&color=fff";

    // 2. Render Components
    renderStatsRow();
    renderRevenuePanel();
    renderTableMap();
}

// Render 4 thẻ bài trên cùng
function renderStatsRow() {
    const container = document.getElementById("stats-container");
    const s = managerData.stats;

    // Config hiển thị cho từng loại thẻ
    const cards = [
        { label: "Tổng Bàn", data: s.totalTables },
        { label: "Món Ăn", data: s.dishes },
        { label: "Đơn Hàng", data: s.orders },
        { label: "Doanh Thu Hôm Nay", data: s.revenueToday }
    ];

    container.innerHTML = cards.map(card => `
        <div class="stat-card">
            <span class="stat-label">${card.label}</span>
            <span class="stat-number" style="color:#2E25D1">${card.data.value}</span>
            <span class="stat-desc">${card.data.note}</span>
        </div>
    `).join('');
}

// Render Panel Doanh Thu (Trái)
function renderRevenuePanel() {
    const d = managerData.revenueDetail;

    document.getElementById("revenue-amount").textContent = d.total;

    const growthEl = document.getElementById("revenue-growth");
    growthEl.textContent = `+${d.growth}% so với hôm qua`;
    // Nếu âm thì đổi màu (logic demo)
    if(d.growth < 0) {
        growthEl.classList.remove("positive");
        growthEl.style.color = "red";
    }

    document.getElementById("rev-orders-count").textContent = d.ordersCount;
    document.getElementById("rev-invoices-count").textContent = d.invoicesCount;
}

// Render Grid Bàn (Phải)
function renderTableMap() {
    const container = document.getElementById("table-map-container");

    container.innerHTML = managerData.tables.map(table => {
        // Map status code sang class CSS
        let cssClass = "";
        if (table.status === 0) cssClass = "status-empty";      // Xanh
        if (table.status === 1) cssClass = "status-occupied";   // Đỏ/Hồng
        if (table.status === 2) cssClass = "status-reserved";   // Vàng

        return `
            <div class="table-card ${cssClass}">
                <span class="tbl-id">#${table.id}</span>
                <span class="tbl-status">${table.name}</span>
            </div>
        `;
    }).join('');
}

// Logic chuyển Tab (Demo)
function setupTabs() {
    const tabs = document.querySelectorAll(".tab-item");
    tabs.forEach(tab => {
        tab.addEventListener("click", function() {
            // Remove active cũ
            document.querySelectorAll(".tab-item").forEach(t => t.classList.remove("active"));
            // Add active mới
            this.classList.add("active");

            // Logic ẩn hiện view (nếu có các view khác)
            // const tabName = this.getAttribute("data-tab");
            // console.log("Switch to tab:", tabName);
        });
    });
}