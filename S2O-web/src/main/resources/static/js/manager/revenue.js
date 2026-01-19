const DEFAULT_IMG_BASE64 = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiB2aWV3Qm94PSIwIDAgMTUwIDE1MCI+PHJlY3Qgd2lkdGg9IjE1MCIgaGVpZ2h0PSIxNTAiIGZpbGw9IiNlZWVlZWUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjIwIiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+Tk8gSU1BR0U8L3RleHQ+PC9zdmc+';
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
                <div class="stat-desc">${item.sub}</div>
            </div>
            <div class="stat-icon">
                <i class="fa-solid ${item.icon}"></i>
            </div>
        </div>
    `).join('');
}

// 1. Khai báo biến toàn cục để quản lý biểu đồ (đặt ở đầu file)
let revenueChartInstance = null;

// ... (Giữ nguyên các đoạn code load ảnh, renderSummary...)

// 2. Thay thế hoàn toàn hàm renderChart cũ bằng hàm này:
function renderChart(chartData) {
    const ctx = document.getElementById('revenueChart').getContext('2d');

    // Nếu không có dữ liệu hoặc dữ liệu rỗng
    if (!chartData || chartData.length === 0) {
        return;
    }

    // Tách dữ liệu ra 2 mảng: Ngày (Trục ngang) và Tiền (Trục dọc)
    // Backend trả về: chartData = [{day: "17/01", value: 500000}, ...]
    const labels = chartData.map(d => d.day);
    const values = chartData.map(d => d.revenue);

    // Hủy biểu đồ cũ nếu đang tồn tại (để vẽ cái mới không bị đè lên)
    if (revenueChartInstance) {
        revenueChartInstance.destroy();
    }

    // Tạo màu nền Gradient (xanh nhạt dần xuống dưới)
    let gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, 'rgba(54, 162, 235, 0.5)'); // Màu xanh đậm ở trên
    gradient.addColorStop(1, 'rgba(54, 162, 235, 0.0)'); // Mờ dần thành trong suốt ở dưới

    // Khởi tạo Chart.js
    revenueChartInstance = new Chart(ctx, {
        type: 'line', // Dạng biểu đồ đường
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu',
                data: values,
                borderColor: '#36A2EB',       // Màu đường kẻ (Xanh dương)
                backgroundColor: gradient,    // Màu nền tô bên dưới
                borderWidth: 3,               // Độ dày đường kẻ
                pointBackgroundColor: '#fff', // Màu chấm tròn
                pointBorderColor: '#36A2EB',  // Viền chấm tròn
                pointHoverBackgroundColor: '#36A2EB',
                pointHoverBorderColor: '#fff',
                fill: true,                   // Bật chế độ tô màu nền
                tension: 0.4                  // 0.4 giúp đường cong mềm mại (0 là đường thẳng gấp khúc)
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }, // Ẩn chú thích (vì chỉ có 1 đường)
                tooltip: {
                    callbacks: {
                        // Format tiền trong hộp thoại khi di chuột vào (VD: 1.5tr)
                        label: function(context) {
                            let value = context.parsed.y;
                            return ' Doanh thu: ' + formatMoneyShort(value);
                        }
                    }
                }
            },
            scales: {
                y: { // Trục dọc (Tiền)
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return formatMoneyShort(value); // Format trục dọc: 1tr, 500k...
                        }
                    },
                    grid: { borderDash: [2, 4], color: '#f0f0f0' } // Kẻ lưới nét đứt mờ
                },
                x: { // Trục ngang (Ngày)
                    grid: { display: false } // Ẩn lưới dọc cho thoáng
                }
            }
        }
    });
}

// Hàm phụ trợ: Format tiền cho gọn (VD: 1500000 -> 1.5tr)
function formatMoneyShort(amount) {
    if (amount >= 1_000_000) {
        return (amount / 1_000_000).toFixed(1).replace('.0', '') + 'tr';
    } else if (amount >= 1_000) {
        return (amount / 1_000).toFixed(0) + 'k';
    }
    return amount.toLocaleString('vi-VN') + 'đ';
}

function renderTopDishes(topDishes) {
    const container = document.getElementById("top-dishes-container");
    if (!topDishes || topDishes.length === 0) {
        container.innerHTML =
            "<div style='text-align:center; padding:20px'>Chưa có dữ liệu món ăn</div>";
        return;
    }

    container.innerHTML = topDishes.map((d, index) => {
        let rawImg = d.img || d.imageUrl;

        let imgUrl = DEFAULT_IMG_BASE64; // Mặc định là ảnh xám

        if (rawImg && rawImg.trim() !== "") {
            if (!rawImg.startsWith("http") && !rawImg.startsWith("data:") && !rawImg.startsWith("/")) {
                imgUrl = "/" + rawImg;
            } else {
                imgUrl = rawImg;
            }
        }

        const totalFormatted = d.total.toLocaleString('vi-VN') + "đ";

        return `
        <div class="top-item">
            <div class="rank-badge">${index + 1}</div>
            <img src="${imgUrl}" class="dish-thumb"
             onerror="this.src='${DEFAULT_IMG_BASE64}'">
            <div class="dish-info">
                <span class="dish-name">${d.name}</span>
                <span class="dish-count">${d.count} phần</span>
            </div>
            <div class="dish-total">${totalFormatted}</div>
        </div>
        `;
    }).join('');
}
