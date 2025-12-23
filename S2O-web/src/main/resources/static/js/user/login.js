document.addEventListener('DOMContentLoaded', function() {

    const togglePasswordBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const iconSpan = togglePasswordBtn.querySelector('.icon-eye');

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