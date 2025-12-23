document.addEventListener('DOMContentLoaded', function() {

    // 1. Xử lý nút Xóa User
    const deleteBtns = document.querySelectorAll('.btn-delete');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const name = row.querySelector('.fw-bold').innerText;

            if (confirm(`Bạn có chắc chắn muốn xóa người dùng "${name}"? Hành động này không thể hoàn tác.`)) {
                row.style.opacity = '0.5';
                setTimeout(() => {
                    row.remove();
                    // Cập nhật số liệu giả lập
                    updateStatValue(0, -1);
                }, 300);
            }
        });
    });

    // 2. Xử lý nút Kích hoạt (Check)
    const checkBtns = document.querySelectorAll('.btn-check');
    checkBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const badge = row.querySelector('.badge');
            const name = row.querySelector('.fw-bold').innerText;

            if (badge.classList.contains('badge-inactive')) {
                if (confirm(`Kích hoạt lại tài khoản cho "${name}"?`)) {
                    badge.className = 'badge badge-active';
                    badge.innerText = 'Hoạt động';
                    alert(`Đã mở khóa tài khoản: ${name}`);
                    updateStatValue(1, 1);
                }
            } else {
                alert(`Tài khoản "${name}" đang hoạt động bình thường.`);
            }
        });
    });

    // Helper: Cập nhật số trên card thống kê
    function updateStatValue(cardIndex, change) {
        const stats = document.querySelectorAll('.stat-value');
        if (stats[cardIndex]) {
            let current = parseInt(stats[cardIndex].innerText.replace(/,/g, ''));
            stats[cardIndex].innerText = (current + change).toLocaleString();
        }
    }
});