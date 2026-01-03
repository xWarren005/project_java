document.addEventListener("DOMContentLoaded", () => {
    if (typeof renderMenu === "function") {
        renderMenu('revenue');
    }
    fetchRevenueData();
});

async function fetchRevenueData() {
    try {
        const response = await fetch('/api/manager/revenue-stats');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        renderSummary(data.summary);
        renderChart(data.chartData);     // ✅ đúng key backend
        renderTopDishes(data.top5Dishes); // ✅ đúng key backend

    } catch (error) {
        console.error("Lỗi tải dữ liệu:", error);
        document.getElementById("summary-container").innerHTML =
            `<p style="color:red">Không thể tải dữ liệu báo cáo. Vui lòng thử lại sau.</p>`;
    }
}

function renderSummary(summaryData) {
    const container = document.getElementById("summary-container");
    if (!summaryData) return;

    container.innerHTML = summaryData.map(item => `
        <div class="stat-card">
            <div>
                <div class="stat-title">${item.title}</div>
                <div class="stat-value">${item.value}</div>
                <div class="stat-desc">${item.subtitle}</div> <!-- ✅ FIX -->
            </div>
            <div class="stat-icon">
                <i class="fa-solid ${item.icon}"></i>
            </div>
        </div>
    `).join('');
}

function renderChart(chartData) {
    const container = document.getElementById("chart-container");
    if (!chartData || chartData.length === 0) {
        container.innerHTML =
            "<div style='text-align:center; padding:20px'>Chưa có dữ liệu doanh thu tuần này</div>";
        return;
    }

    const maxVal = Math.max(...chartData.map(d => d.value));

    container.innerHTML = chartData.map(d => {
        const percent = maxVal > 0 ? (d.value / maxVal) * 100 : 0;

        let displayRev = d.value >= 1_000_000
            ? (d.value / 1_000_000).toFixed(1) + "tr"
            : d.value >= 1_000
                ? (d.value / 1_000).toFixed(0) + "k"
                : "0đ";

        return `
            <div class="bar-row">
                <div class="bar-label">${d.day}</div>
                <div class="bar-track">
                    <div class="bar-fill" style="width: ${percent}%">
                        ${displayRev}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function renderTopDishes(topDishes) {
    const container = document.getElementById("top-dishes-container");
    if (!topDishes || topDishes.length === 0) {
        container.innerHTML =
            "<div style='text-align:center; padding:20px'>Chưa có dữ liệu món ăn</div>";
        return;
    }

    container.innerHTML = topDishes.map((d, index) => {
        const imgUrl = d.imageUrl && d.imageUrl.trim() !== ""
            ? d.imageUrl
            : "https://via.placeholder.com/100?text=No+Img";

        const totalFormatted = d.total.toLocaleString('vi-VN') + "đ";

        return `
        <div class="top-item">
            <div class="rank-badge">${index + 1}</div>
            <img src="${imgUrl}" class="dish-thumb"
                 onerror="this.src='https://via.placeholder.com/50?text=Err'">
            <div class="dish-info">
                <span class="dish-name">${d.name}</span>
                <span class="dish-count">${d.count} phần</span>
            </div>
            <div class="dish-total">${totalFormatted}</div>
        </div>
        `;
    }).join('');
}
