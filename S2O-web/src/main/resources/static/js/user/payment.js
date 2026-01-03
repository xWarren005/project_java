/* ===============================
   PAYMENT LOGIC - API VERSION
   =============================== */
let checkStatusInterval = null;
// Utils format tiền
function formatPaymentPrice(price) {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
}
/**
 * 1. RENDER GIAO DIỆN THANH TOÁN
 */

async function renderPayment() {
    const el = document.getElementById("payment-content")
    if (!el) return;
    // Loading...
    el.innerHTML = '<div style="text-align:center; padding:20px; color:#888">⏳ Đang tải thông tin...</div>';
    try {
        // Tận dụng API lấy hóa đơn tạm tính để lấy tổng tiền
        const response = await fetch('/api/user/invoice/current');

        if (response.ok) {
            const orders = await response.json();

    if (!orders || orders.length === 0) {
        el.innerHTML = `
            <div class="payment-empty">
                Không có đơn hàng để thanh toán
            </div>`;
        return;
    }
// Tính tổng tiền từ dữ liệu API
    const total = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);
    el.innerHTML = `
        <div class="payment-card">
            <div class="payment-total">
                <span>Tổng thanh toán</span>
                <strong style="color:var(--color-secondary); font-size:1.2rem;">${formatPaymentPrice(total)}</strong>
                    </div>

            <div class="payment-methods">
                <label>
                    <input type="radio" name="paymentMethod" value="CASH" checked>
                    <i class="fas fa-money-bill-wave" style="margin-right:5px; color:#27ae60;"></i> Tiền mặt
                </label>
                <label>
                    <input type="radio" name="paymentMethod" value="TRANSFER">
                    <i class="fas fa-qrcode" style="margin-right:5px; color:#2980b9;"></i> Chuyển khoản
                </label>
            </div>
            <button class="btn btn-primary btn-full" 
                        style="width:100%; padding:15px; border-radius:12px; background:var(--color-secondary); color:white; font-weight:bold; border:none; font-size:1rem; cursor:pointer;"
                        onclick="confirmPayment(${total})">
                        yêu cầu thanh toán
                    </button>
                </div>
            `;
            // Logic hiện QR code khi chọn radio button
            document.querySelectorAll('input[name="paymentMethod"]').forEach(input => {
                input.addEventListener('change', (e) => {
                    const qrDiv = document.getElementById('qr-container');
                    qrDiv.style.display = e.target.value === 'TRANSFER' ? 'block' : 'none';
                });
            });
        } else {
            el.innerHTML = '<div class="payment-empty">Lỗi tải dữ liệu</div>';
        }
    } catch (e) {
        console.error(e);
        el.innerHTML = '<div class="payment-empty">Lỗi kết nối</div>';
    }
    checkIfWaitingForCashier();
}
async function checkIfWaitingForCashier() {
    const el = document.getElementById("payment-content");
    try {
        // Gọi API lấy hóa đơn hiện tại để xem trạng thái
        const response = await fetch('/api/user/invoice/current'); // Bạn cần sửa API này trả về cả đơn PAYMENT_PENDING nhé
        if (response.ok) {
            const orders = await response.json();
            // Nếu tìm thấy đơn hàng có trạng thái PAYMENT_PENDING
            const isPending = orders.some(o => o.status === 'PAYMENT_PENDING');

            if (isPending) {
                showWaitingScreen(el);
                startPollingStatus(); // Bắt đầu kiểm tra liên tục
            }
        }
    } catch (e) { console.error(e); }
}
async function confirmPayment(totalAmount) {
    const methodInput = document.querySelector('input[name="paymentMethod"]:checked');
    const method = methodInput ? methodInput.value : "CASH";

    if (!confirm("Gửi yêu cầu thanh toán đến thu ngân?"))
        return;
    try {
        const response = await fetch('/api/user/payment/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ method: method })
        });

        if (response.ok) {
            // Thay vì alert thành công, ta hiện màn hình chờ
            const el = document.getElementById("payment-content");
            showWaitingScreen(el);

            // Bắt đầu lắng nghe xem Thu ngân xác nhận chưa
            startPollingStatus();
        } else {
            const msg = await response.text();
            alert("Lỗi: " + msg);
        }
    } catch (e) {
        alert("Lỗi kết nối!");
    }
}
// 2. MÀN HÌNH CHỜ THU NGÂN
function showWaitingScreen(container) {
    container.innerHTML = `
        <div class="payment-card" style="text-align:center; padding: 40px 20px;">
            <div class="spinner" style="font-size: 3rem; margin-bottom: 20px;">⏳</div>
            <h3 style="color: #d35400; margin-bottom: 10px;">Đã gửi yêu cầu!</h3>
            <p style="color: #666; margin-bottom: 20px;">
                Vui lòng đợi thu ngân xác nhận.<br>
            </p>
            <button class="btn" style="background:#eee; color:#666;" onclick="location.reload()">Làm mới trạng thái</button>
        </div>
    `;
}

// 3. CƠ CHẾ POLLING (TỰ ĐỘNG KIỂM TRA)
function startPollingStatus() {
    if (checkStatusInterval) clearInterval(checkStatusInterval);

    checkStatusInterval = setInterval(async () => {
        try {
            // Kiểm tra xem còn đơn hàng nào chưa PAID không?
            // Nếu list rỗng => Có nghĩa là đã PAID hết rồi (vì API invoice/current chỉ trả về đơn chưa PAID)
            const response = await fetch('/api/user/invoice/current');

            if (response.ok) {
                const orders = await response.json();

                // Logic: Nếu danh sách rỗng HOẶC không còn đơn nào PAYMENT_PENDING (tức là đã chuyển sang PAID hết rồi)
                if (!orders || orders.length === 0) {
                    clearInterval(checkStatusInterval);
                    alert("Thanh toán thành công!");
                    window.location.href = "/user/history"; // Chuyển sang lịch sử
                }
            }
        } catch (e) {
            console.error("Lỗi kiểm tra trạng thái", e);
        }
    }, 3000); // Kiểm tra mỗi 3 giây
}