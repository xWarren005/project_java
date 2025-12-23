// Biến toàn cục để chứa dữ liệu
let overviewData = {
    stats: {},
    revenueDetail: {},
    tables: []
};

// --- LOGIC CHẠY KHI TRANG LOAD ---
document.addEventListener("DOMContentLoaded", () => {
    console.log("Loading Overview Page...");
    fetchDashboardData(); // Gọi hàm lấy dữ liệu từ server
});

// Hàm gọi API Java
function fetchDashboardData() {
    fetch('/api/manager/overview')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            // Gán dữ liệu từ Server vào biến overviewData
            overviewData = data;

            // Render lại giao diện
            renderStatsRow();
            renderRevenuePanel();
            renderTableMap();
        })
        .catch(error => console.error('Error fetching data:', error));
}

function renderStatsRow() {
    const container = document.getElementById("stats-container");
    const s = overviewData.stats;

    // Kiểm tra dữ liệu có tồn tại không trước khi render
    if (!s) return;

    const cards = [
        {l: "Tổng Bàn", d: s.totalTables},
        {l: "Món Ăn", d: s.dishes},
        {l: "Đơn Hàng", d: s.orders},
        {l: "Doanh Thu", d: s.revenueToday}
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
    if (!d) return;

    document.getElementById("revenue-amount").textContent = d.total;
    // Xử lý hiển thị tăng trưởng
    const growthSign = d.growth >= 0 ? "+" : "";
    document.getElementById("revenue-growth").textContent = `${growthSign}${d.growth}% hôm nay`;

    document.getElementById("rev-orders-count").textContent = d.ordersCount;
    document.getElementById("rev-invoices-count").textContent = d.invoicesCount;
}

function renderTableMap() {
    const container = document.getElementById("table-map-container");
    if (!overviewData.tables) return;

    container.innerHTML = overviewData.tables.map(t => {
        // Status: 0=Empty, 1=Occupied, 2=Reserved (đã xử lý ở Java Service)
        let cls = t.status === 1 ? "status-occupied" : (t.status === 2 ? "status-reserved" : "status-empty");
        // t.name ở đây tương ứng với table_name trong DB
        return `<div class="table-card ${cls}">
                    <span class="tbl-id">#${t.id}</span>
                    <span class="tbl-status">${t.name}</span>
                </div>`;
    }).join('');
}