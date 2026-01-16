document.addEventListener('DOMContentLoaded', function() {
    fetchDashboardData();
});

async function fetchDashboardData() {
    try {
        const response = await fetch('/api/admin/dashboard/stats');
        if (!response.ok) throw new Error("Lỗi tải dữ liệu");

        const data = await response.json();

        // 1. Fill Stats Cards
        // Chú ý: data.stats trả về mảng theo thứ tự trong Service
        if (data.stats) {
            document.getElementById('stat-active-res').innerText = data.stats[0].value;
            document.getElementById('stat-total-users').innerText = data.stats[1].value;
            document.getElementById('stat-revenue').innerText = data.stats[2].value;
            document.getElementById('stat-system').innerText = data.stats[3].value;
        }

        // 2. Render Chart (Dữ liệu thật)
        renderChart(data.chartData);

        // 3. Render Nhà hàng mới
        renderRestaurants(data.newRestaurants);

        // 4. Render Activities (Đơn hàng mới)
        renderActivities(data.activities);

    } catch (e) {
        console.error("Dashboard Error:", e);
    }
}

function renderChart(chartData) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    if (window.myRevenueChart) window.myRevenueChart.destroy();

    window.myRevenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels, // ["Tháng 1", "Tháng 2"...] từ API
            datasets: [{
                label: 'Doanh thu',
                data: chartData.data, // [100000, 200000...] từ API
                borderColor: '#4f46e5',
                backgroundColor: 'rgba(79, 70, 229, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { beginAtZero: true } }
        }
    });
}

function renderRestaurants(list) {
    const container = document.getElementById('restaurantListContainer');
    if (!list || list.length === 0) {
        container.innerHTML = '<div style="padding:20px; text-align:center">Không có dữ liệu</div>';
        return;
    }

    container.innerHTML = list.map(item => `
        <div class="reg-item">
            <div class="reg-info">
                <strong>${item.name}</strong>
                <span>${item.address}</span>
            </div>
            <div class="reg-action">
                <span class="status-badge ${item.status === 'ACTIVE' ? 'success' : 'warning'}">
                    ${item.status}
                </span>
            </div>
        </div>
    `).join('');
}

function renderActivities(list) {
    const container = document.getElementById('activityTableBody');
    if (!list || list.length === 0) {
        container.innerHTML = '<tr><td colspan="5" style="text-align:center">Chưa có đơn hàng nào</td></tr>';
        return;
    }

    container.innerHTML = list.map(item => `
        <tr>
            <td>${item.time}</td>
            <td>${item.user}</td>
            <td>${item.action}</td>
            <td>${item.details}</td>
            <td><span class="status-badge ${item.statusColor}">${item.status}</span></td>
        </tr>
    `).join('');
}