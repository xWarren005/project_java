document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('togglePassword');
    const passInput = document.getElementById('password');

    if (toggleBtn && passInput) {
        toggleBtn.addEventListener('click', function() {
            const type = passInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passInput.setAttribute('type', type);

            // Đổi độ mờ của icon mắt để báo hiệu trạng thái
            this.style.opacity = type === 'text' ? '1' : '0.5';
        });
    }
});