document.addEventListener('DOMContentLoaded', function() {
    // Vẽ biểu đồ Doanh thu (sử dụng Chart.js)
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

    // Sự kiện nút duyệt
    const btns = document.querySelectorAll('.btn-approve');
    btns.forEach(btn => {
        btn.addEventListener('click', () => {
            btn.innerText = 'Đã duyệt';
            btn.style.backgroundColor = '#10b981';
            btn.disabled = true;
        });
    });
});