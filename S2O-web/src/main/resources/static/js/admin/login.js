document.addEventListener('DOMContentLoaded', function() {
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
                    iconSpan.textContent = '🔓';
                    this.style.opacity = '1';
                } else {
                    iconSpan.textContent = '👁️';
                    this.style.opacity = '0.6';
                }
            }
        });
    }
});