// --- DATA GIẢ LẬP CHO OVERVIEW ---
const overviewData = {
    stats: {
        totalTables: {value: 12, note: "4 bàn đang có khách"},
        dishes: {value: 24, note: "22 món đang phục vụ"},
        orders: {value: 8, note: "3 đơn đang chế biến"},
        revenueToday: {value: "5.2tr", note: "+12% so với hôm qua"}
    },
    revenueDetail: {total: "5.2tr VNĐ", growth: 11.8, ordersCount: 2, invoicesCount: 0},
    tables: [
        {id: 1, status: 0, name: "Trống"}, {id: 2, status: 1, name: "Có Khách"},
        {id: 3, status: 2, name: "Đã Đặt"}, {id: 4, status: 0, name: "Trống"},
        {id: 5, status: 1, name: "Có Khách"}, {id: 6, status: 2, name: "Đã Đặt"},
        {id: 7, status: 0, name: "Trống"}, {id: 8, status: 1, name: "Có Khách"}
    ]
};

// --- LOGIC CHẠY KHI TRANG LOAD ---
document.addEventListener("DOMContentLoaded", () => {
    console.log("Loading Overview Page...");
    renderStatsRow();
    renderRevenuePanel();
    renderTableMap();
});

function renderStatsRow() {
    const container = document.getElementById("stats-container");
    const s = overviewData.stats;
    const cards = [
        {l: "Tổng Bàn", d: s.totalTables}, {l: "Món Ăn", d: s.dishes},
        {l: "Đơn Hàng", d: s.orders}, {l: "Doanh Thu", d: s.revenueToday}
    ];
    container.innerHTML = cards.map(c => `
        <div class="stat-card">
            <span class="stat-label">${c.l}</span>
            <span class="stat-number">${c.d.value}</span>
            <span class="stat-desc">${c.d.note}</span>
        </div>
    `).join('');
}

function renderRevenuePanel() {
    const d = overviewData.revenueDetail;
    document.getElementById("revenue-amount").textContent = d.total;
    document.getElementById("revenue-growth").textContent = `+${d.growth}% hôm nay`;
    document.getElementById("rev-orders-count").textContent = d.ordersCount;
    document.getElementById("rev-invoices-count").textContent = d.invoicesCount;
}

function renderTableMap() {
    const container = document.getElementById("table-map-container");
    container.innerHTML = overviewData.tables.map(t => {
        let cls = t.status === 1 ? "status-occupied" : (t.status === 2 ? "status-reserved" : "status-empty");
        return `<div class="table-card ${cls}"><span class="tbl-id">#${t.id}</span><span class="tbl-status">${t.name}</span></div>`;
    }).join('');
}