document.addEventListener('DOMContentLoaded', () => {

    // =========================================
    // 1. MOCK DATA & VARIABLES
    // =========================================
    let alertsData = [
        {
            id: 1,
            type: 'success', // success, warning, info, error
            message: 'Hệ thống hoạt động bình thường',
            time: '2 phút trước'
        },
        {
            id: 2,
            type: 'warning',
            message: 'CPU usage tăng cao tại Server 3',
            time: '15 phút trước'
        },
        {
            id: 3,
            type: 'info',
            message: 'Backup database hoàn tất',
            time: '1 giờ trước'
        },
        {
            id: 4,
            type: 'error',
            message: 'Lỗi kết nối API bên thứ 3 (Payment Gateway)',
            time: '2 giờ trước'
        },
        {
            id: 5,
            type: 'info',
            message: 'Đã cập nhật bản vá bảo mật v1.2',
            time: '5 giờ trước'
        }
    ];

    // =========================================
    // 2. DOM ELEMENTS
    // =========================================
    // Metrics Elements
    const cpuValue = document.getElementById('cpuValue');
    const cpuBar = document.getElementById('cpuBar');
    const memValue = document.getElementById('memValue');
    const memBar = document.getElementById('memBar');
    const apiValue = document.querySelector('.metric-card:nth-child(3) .metric-value');
    const errorValue = document.querySelector('.metric-card:nth-child(4) .metric-value');

    // Alert Elements
    const alertsList = document.getElementById('alertsList');
    const searchInput = document.getElementById('searchLogs');

    // =========================================
    // 3. HELPER FUNCTIONS
    // =========================================

    // Hàm xác định class CSS dựa trên loại cảnh báo
    const getAlertClass = (type) => {
        switch (type) {
            case 'success': return 'alert-success';
            case 'warning': return 'alert-warning';
            case 'error':   return 'alert-error';
            case 'info':    return 'alert-info';
            default:        return 'alert-info';
        }
    };

    // Hàm đổi màu thanh progress bar dựa trên % sử dụng
    const updateProgressBarColor = (element, percentage) => {
        element.style.width = `${percentage}%`;

        // Reset colors
        element.style.backgroundColor = '#3b82f6'; // Default Blue

        if (percentage > 80) {
            element.style.backgroundColor = '#ef4444'; // Red (Danger)
        } else if (percentage > 60) {
            element.style.backgroundColor = '#f59e0b'; // Yellow (Warning)
        }
    };

    // =========================================
    // 4. RENDER FUNCTIONS
    // =========================================

    const renderAlerts = (data) => {
        alertsList.innerHTML = ''; // Clear old content

        if (data.length === 0) {
            alertsList.innerHTML = `<div style="text-align:center; padding: 20px; color:#999;">Không tìm thấy cảnh báo nào</div>`;
            return;
        }

        data.forEach(alert => {
            const item = document.createElement('div');
            item.className = `alert-item ${getAlertClass(alert.type)}`;
            item.innerHTML = `
                <div class="alert-content">
                    <span class="alert-dot"></span>
                    <span class="alert-message">${alert.message}</span>
                </div>
                <span class="alert-time">${alert.time}</span>
            `;
            alertsList.appendChild(item);
        });
    };

    // =========================================
    // 5. LIVE SIMULATION (Mô phỏng thời gian thực)
    // =========================================

    const updateSystemMetrics = () => {
        // 1. Simulate CPU (Random 20% - 95%)
        const cpu = Math.floor(Math.random() * (95 - 20 + 1)) + 20;
        cpuValue.innerText = `${cpu}%`;
        updateProgressBarColor(cpuBar, cpu);

        // 2. Simulate Memory (Random 40% - 90%)
        const mem = Math.floor(Math.random() * (90 - 40 + 1)) + 40;
        memValue.innerText = `${mem}%`;
        updateProgressBarColor(memBar, mem);

        // 3. Simulate API Latency (Random 50ms - 300ms)
        const latency = Math.floor(Math.random() * (300 - 50 + 1)) + 50;
        apiValue.innerText = `${latency}ms`;
        // Đổi màu text nếu latency cao
        apiValue.nextElementSibling.className = latency > 200 ? 'metric-status text-red' : 'metric-status text-green';
        apiValue.nextElementSibling.innerText = latency > 200 ? 'Chậm' : 'Tốt';

        // 4. Simulate Error Rate (Very low random)
        const err = (Math.random() * 2).toFixed(2); // 0.00 - 2.00
        errorValue.innerText = `${err}%`;
    };

    // Hàm tạo cảnh báo ngẫu nhiên (chạy mỗi 10 giây)
    const generateRandomAlert = () => {
        const types = ['info', 'warning', 'success', 'error'];
        const messages = [
            'Đồng bộ dữ liệu hoàn tất',
            'Phát hiện truy cập bất thường IP 192.168.1.x',
            'Service "Payment" phản hồi chậm',
            'Người dùng admin đăng nhập thành công',
            'Đã giải phóng bộ nhớ Cache',
            'Cronjob "Daily Report" chạy thành công'
        ];

        const randomType = types[Math.floor(Math.random() * types.length)];
        const randomMsg = messages[Math.floor(Math.random() * messages.length)];

        const newAlert = {
            id: Date.now(),
            type: randomType,
            message: randomMsg,
            time: 'Vừa xong'
        };

        // Thêm vào đầu mảng
        alertsData.unshift(newAlert);

        // Giữ lại tối đa 10 cảnh báo để không bị dài quá
        if (alertsData.length > 10) alertsData.pop();

        // Nếu người dùng đang KHÔNG tìm kiếm thì mới render lại (để không làm phiền lúc đang search)
        if (searchInput.value === '') {
            renderAlerts(alertsData);
        }
    };

    // =========================================
    // 6. EVENT LISTENERS
    // =========================================

    // Search functionality
    searchInput.addEventListener('input', (e) => {
        const keyword = e.target.value.toLowerCase();
        const filtered = alertsData.filter(item =>
            item.message.toLowerCase().includes(keyword)
        );
        renderAlerts(filtered);
    });

    // =========================================
    // 7. INITIALIZATION & INTERVALS
    // =========================================

    // Render ban đầu
    renderAlerts(alertsData);

    // Chạy update metrics mỗi 2 giây
    setInterval(updateSystemMetrics, 2000);

    // Sinh cảnh báo mới mỗi 8 giây
    setInterval(generateRandomAlert, 8000);

});