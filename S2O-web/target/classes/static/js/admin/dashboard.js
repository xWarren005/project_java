document.addEventListener('DOMContentLoaded', function() {
    // 1. Vẽ biểu đồ Doanh thu (Chart.js)
    const ctx = document.getElementById('revenueChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6'],
                datasets: [{
                    label: 'Doanh thu',
                    data: [15000, 22000, 18000, 30000, 35000, 48392],
                    borderColor: '#4f46e5',
                    backgroundColor: 'rgba(79, 70, 229, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#4f46e5',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, grid: { borderDash: [2, 4] } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
});

/**
 * 2. Hàm sắp xếp danh sách nhà hàng
 * Hàm này cần nằm ngoài DOMContentLoaded để HTML có thể gọi được qua onchange=""
 */
function sortList() {
    const list = document.getElementById('restaurantList');
    const sortValue = document.getElementById('sortFilter').value;

    // Lấy tất cả các item con và chuyển thành mảng để sort
    const items = Array.from(list.getElementsByClassName('reg-item'));

    items.sort((a, b) => {
        // Lấy giá trị timestamp từ thuộc tính data-timestamp
        const timeA = parseInt(a.getAttribute('data-timestamp'));
        const timeB = parseInt(b.getAttribute('data-timestamp'));

        if (sortValue === 'newest') {
            return timeB - timeA; // Giảm dần (Số lớn/Mới nhất lên đầu)
        } else {
            return timeA - timeB; // Tăng dần (Số nhỏ/Cũ nhất lên đầu)
        }
    });

    // Xóa danh sách hiện tại và thêm lại theo thứ tự mới
    list.innerHTML = '';
    items.forEach(item => list.appendChild(item));
}