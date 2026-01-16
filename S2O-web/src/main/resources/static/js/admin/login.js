document.addEventListener('DOMContentLoaded', function() {

    // ============================================================
    // 1. XỬ LÝ ẨN/HIỆN MẬT KHẨU (Code của bạn)
    // ============================================================
    const toggleBtn = document.getElementById('togglePassword');
    const passInput = document.getElementById('password');

    if (toggleBtn && passInput) {
        toggleBtn.addEventListener('click', function(e) {
            e.preventDefault();

            const currentType = passInput.getAttribute('type');
            const newType = currentType === 'password' ? 'text' : 'password';
            passInput.setAttribute('type', newType);

            const iconSpan = this.querySelector('span');
            if (iconSpan) {
                if (newType === 'text') {
                    iconSpan.textContent = '🔓'; // Icon mở khóa
                    this.style.opacity = '1';
                } else {
                    iconSpan.textContent = '👁️'; // Icon mắt
                    this.style.opacity = '0.6';
                }
            }
        });
    }

    // ============================================================
    // 2. [MỚI] XỬ LÝ HIỆU ỨNG KHI CHỌN VAI TRÒ (ROLE)
    // Giúp người dùng biết mình đang chọn vai trò nào
    // ============================================================
    const roleInputs = document.querySelectorAll('input[name="role"]');

    function updateRoleVisuals() {
        roleInputs.forEach(input => {
            // Tìm thẻ div .role-card nằm cùng trong label
            // Cấu trúc HTML: label > input + div.role-card
            const card = input.closest('.role-option').querySelector('.role-card');

            if (input.checked) {
                // Nếu được chọn: Thêm viền màu xanh và nền nhạt
                card.style.borderColor = '#2563eb';      // Xanh dương đậm
                card.style.backgroundColor = '#eff6ff';  // Xanh dương rất nhạt
                card.style.boxShadow = '0 4px 6px -1px rgba(37, 99, 235, 0.2)';
                card.style.transform = 'translateY(-2px)'; // Nhích lên nhẹ
                card.style.transition = 'all 0.2s ease';
            } else {
                // Nếu không chọn: Trả về bình thường
                card.style.borderColor = '#e5e7eb';      // Xám nhạt
                card.style.backgroundColor = '#fff';     // Trắng
                card.style.boxShadow = 'none';
                card.style.transform = 'none';
            }
        });
    }

    // Gán sự kiện cho tất cả các nút radio role
    roleInputs.forEach(input => {
        input.addEventListener('change', updateRoleVisuals);
    });

    // Chạy 1 lần ngay khi tải trang (để highlight cái nào đang được checked mặc định)
    updateRoleVisuals();
});