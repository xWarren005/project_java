document.addEventListener('DOMContentLoaded', function() {

    const togglePasswordBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const iconSpan = togglePasswordBtn.querySelector('.icon-eye');
    const backBtn = document.getElementById("btn-back-welcome");

    if (backBtn) {
        backBtn.addEventListener("click", (e) => {
            e.preventDefault(); // Chặn hành vi mặc định của thẻ a

            // 1. Lấy ID bàn đã lưu từ lúc quét QR
            const tableId = localStorage.getItem("currentTableId");

            if (tableId) {
                // 2. Chuyển hướng kèm tham số tableId
                window.location.href = `/user/welcome?tableId=${tableId}`;
            } else {
                // Trường hợp mất localStorage (do mở tab ẩn danh mới hoặc xóa cache)
                alert("Không tìm thấy thông tin bàn. Vui lòng quét lại mã QR.");
            }
        });
    }
    if (togglePasswordBtn && passwordInput) {

        togglePasswordBtn.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';

            passwordInput.setAttribute('type', type);

            if (type === 'text') {
                togglePasswordBtn.style.opacity = '1';
            } else {
                togglePasswordBtn.style.opacity = '0.4';
            }
        });
    }
});