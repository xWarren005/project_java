document.addEventListener('DOMContentLoaded', () => {

    // =========================================
    // 1. SELECTORS
    // =========================================
    const searchInput = document.querySelector('.search-input');
    const configCards = document.querySelectorAll('.config-card');

    // Forms
    const recForm = document.getElementById('recommendationForm');
    const chatbotForm = document.getElementById('chatbotForm');

    // Recommendation Inputs
    // Lưu ý: Dựa vào thứ tự phần tử trong HTML vì không có ID cụ thể
    const vectorWeightInput = recForm.querySelector('.form-group:nth-child(1) input');
    const ruleWeightInput = recForm.querySelector('.form-group:nth-child(2) input');

    // Chatbot Inputs
    const modelSelect = chatbotForm.querySelector('select');
    const tempInput = chatbotForm.querySelector('.form-group:nth-child(2) input');
    const tokenInput = chatbotForm.querySelector('.form-group:nth-child(3) input');

    // Performance Elements
    const perfAccuracy = document.querySelector('.perf-item:nth-child(1) .perf-value');
    const perfTime = document.querySelector('.perf-item:nth-child(2) .perf-value');

    // =========================================
    // 2. HELPER FUNCTIONS
    // =========================================

    // Hàm hiển thị thông báo (Custom Toast đơn giản)
    const showNotification = (message, type = 'success') => {
        // Tạo element thông báo
        const toast = document.createElement('div');
        toast.textContent = message;
        toast.style.position = 'fixed';
        toast.style.bottom = '20px';
        toast.style.right = '20px';
        toast.style.padding = '12px 24px';
        toast.style.borderRadius = '8px';
        toast.style.color = '#fff';
        toast.style.fontWeight = '500';
        toast.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
        toast.style.zIndex = '1000';
        toast.style.transition = 'all 0.3s ease';
        toast.style.transform = 'translateY(100%)';
        toast.style.opacity = '0';

        // Màu sắc dựa trên loại thông báo
        if (type === 'success') toast.style.backgroundColor = '#10b981'; // Green
        if (type === 'error') toast.style.backgroundColor = '#ef4444';   // Red

        document.body.appendChild(toast);

        // Animation hiện lên
        requestAnimationFrame(() => {
            toast.style.transform = 'translateY(0)';
            toast.style.opacity = '1';
        });

        // Tự động tắt sau 3 giây
        setTimeout(() => {
            toast.style.transform = 'translateY(100%)';
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    };

    // =========================================
    // 3. EVENT HANDLERS
    // =========================================

    // --- A. Xử lý Form Recommendation Engine ---
    recForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const vectorVal = parseFloat(vectorWeightInput.value);
        const ruleVal = parseFloat(ruleWeightInput.value);

        // Logic: Tổng 2 trọng số phải xấp xỉ 1.0
        // Dùng Math.abs để so sánh số thực (floating point)
        if (Math.abs((vectorVal + ruleVal) - 1.0) > 0.001) {
            showNotification(`Lỗi: Tổng trọng số phải bằng 1.0 (Hiện tại: ${(vectorVal + ruleVal).toFixed(1)})`, 'error');
            return;
        }

        // Logic giả lập lưu dữ liệu
        const btn = recForm.querySelector('button');
        const originalText = btn.textContent;
        btn.textContent = 'Đang lưu...';
        btn.disabled = true;

        setTimeout(() => {
            btn.textContent = originalText;
            btn.disabled = false;

            // Cập nhật text hiển thị bên dưới input
            vectorWeightInput.nextElementSibling.textContent = `Hiện tại: ${vectorVal}`;
            ruleWeightInput.nextElementSibling.textContent = `Hiện tại: ${ruleVal}`;

            showNotification('Đã lưu cấu hình Recommendation Engine thành công!');
        }, 1000);
    });

    // --- B. Xử lý Form Chatbot QA ---
    chatbotForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const tempVal = parseFloat(tempInput.value);
        const tokenVal = parseInt(tokenInput.value);

        // Validate
        if (tempVal < 0 || tempVal > 1) {
            showNotification('Temperature phải nằm trong khoảng 0 đến 1', 'error');
            return;
        }

        if (tokenVal <= 0 || tokenVal > 4096) {
            showNotification('Max Tokens không hợp lệ (1 - 4096)', 'error');
            return;
        }

        // Giả lập lưu
        const btn = chatbotForm.querySelector('button');
        const originalText = btn.textContent;
        btn.textContent = 'Đang lưu...';
        btn.disabled = true;

        setTimeout(() => {
            btn.textContent = originalText;
            btn.disabled = false;

            // Cập nhật text hiển thị
            tempInput.nextElementSibling.textContent = `Hiện tại: ${tempVal}`;

            showNotification(`Đã cập nhật model ${modelSelect.value} thành công!`);
        }, 8000);
    });

    // --- C. Xử lý Tìm kiếm ---
    searchInput.addEventListener('input', (e) => {
        const keyword = e.target.value.toLowerCase();

        configCards.forEach(card => {
            const title = card.querySelector('h3').textContent.toLowerCase();
            const desc = card.querySelector('p').textContent.toLowerCase();

            if (title.includes(keyword) || desc.includes(keyword)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });

    // =========================================
    // 4. AUTO-ADJUST INPUTS (UX Improvement)
    // =========================================

    // Khi thay đổi Vector Weight, tự động gợi ý Rule Weight để tổng = 1
    vectorWeightInput.addEventListener('change', (e) => {
        let val = parseFloat(e.target.value);
        if (val > 1) val = 1;
        if (val < 0) val = 0;

        e.target.value = val;
        ruleWeightInput.value = (1 - val).toFixed(1);
    });

    ruleWeightInput.addEventListener('change', (e) => {
        let val = parseFloat(e.target.value);
        if (val > 1) val = 1;
        if (val < 0) val = 0;

        e.target.value = val;
        vectorWeightInput.value = (1 - val).toFixed(1);
    });

    // =========================================
    // 5. LIVE PERFORMANCE SIMULATION
    // =========================================
    // Giả lập số liệu nhảy nhẹ mỗi 3 giây để màn hình trông "sống động"

    setInterval(() => {
        // Random accuracy: 94.0% -> 95.0%
        const randomAcc = (94 + Math.random()).toFixed(1);
        perfAccuracy.textContent = `${randomAcc}%`;

        // Random time: 0.7s -> 0.9s
        const randomTime = (0.7 + Math.random() * 0.2).toFixed(2);
        perfTime.textContent = `${randomTime}s`;
    }, 3000);

});