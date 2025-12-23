// --- DỮ LIỆU GIẢ LẬP (MOCK DATA) ---

// 1. Dữ liệu tổng quan
const summaryData = [
    { title: "Doanh Thu Hôm Nay", value: "5.2tr", sub: "+11.8%", icon: "fa-dollar-sign" },
    { title: "Đơn Hàng", value: "24", sub: "+3 đơn mới", icon: "fa-cart-shopping" },
    { title: "Món Bán Chạy", value: "Phở Bò", sub: "68 bát", icon: "fa-fire" },
    { title: "Khách Hàng", value: "48", sub: "+12 khách", icon: "fa-users" }
];

// 2. Dữ liệu biểu đồ (7 ngày)
const chartData = [
    { day: "Thứ 2", revenue: 4200000 },
    { day: "Thứ 3", revenue: 3800000 },
    { day: "Thứ 4", revenue: 4500000 },
    { day: "Thứ 5", revenue: 5100000 },
    { day: "Thứ 6", revenue: 5800000 },
    { day: "Thứ 7", revenue: 6200000 },
    { day: "CN",    revenue: 5200000 }
];

// 3. Dữ liệu Top món
const topDishes = [
    { id: 1, name: "Phở Bò Đặc Biệt", count: 39, total: 2415000, img: "https://source.unsplash.com/100x100/?pho" },
    { id: 2, name: "Bún Chả Hà Nội", count: 50, total: 1940000, img: "https://source.unsplash.com/100x100/?noodle" },
    { id: 3, name: "Gỏi Cuốn Tôm Thịt", count: 52, total: 2750000, img: "https://source.unsplash.com/100x100/?springroll" },
    { id: 4, name: "Chè Ba Màu", count: 29, total: 2811000, img: "https://source.unsplash.com/100x100/?dessert" },
    { id: 5, name: "Nước Chanh Dây", count: 46, total: 2273000, img: "https://source.unsplash.com/100x100/?drink" }
];

// --- RENDER FUNCTIONS ---

document.addEventListener("DOMContentLoaded", () => {
    renderSummary();
    renderChart();
    renderTopDishes();
});

function renderSummary() {
    const container = document.getElementById("summary-container");
    container.innerHTML = summaryData.map(item => `
        <div class="stat-card">
            <div>
                <div class="stat-title">${item.title}</div>
                <div class="stat-value">${item.value}</div>
                <div class="stat-desc">${item.sub}</div>
            </div>
            <div class="stat-icon"><i class="fa-solid ${item.icon}"></i></div>
        </div>
    `).join('');
}

function renderChart() {
    const container = document.getElementById("chart-container");
    // Tìm giá trị lớn nhất để tính % chiều dài thanh
    const maxVal = Math.max(...chartData.map(d => d.revenue));

    container.innerHTML = chartData.map(d => {
        const percent = (d.revenue / maxVal) * 100;
        const displayRev = (d.revenue / 1000000).toFixed(1) + "tr"; // Đổi sang triệu
        return `
            <div class="bar-row">
                <div class="bar-label">${d.day}</div>
                <div class="bar-track">
                    <div class="bar-fill" style="width: ${percent}%">${displayRev}</div>
                </div>
            </div>
        `;
    }).join('');
}

function renderTopDishes() {
    const container = document.getElementById("top-dishes-container");
    container.innerHTML = topDishes.map((d, index) => `
        <div class="top-item">
            <div class="rank-badge">${index + 1}</div>
            <img src="${d.img}" class="dish-thumb" onerror="this.src='https://via.placeholder.com/50'">
            <div class="dish-info">
                <span class="dish-name">${d.name}</span>
                <span class="dish-count">${d.count} đơn</span>
            </div>
            <div class="dish-total">${d.total.toLocaleString()}đ</div>
        </div>
    `).join('');
}