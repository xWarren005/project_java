/* ============================================================
   GUEST PAYMENT JS - THANH TOÁN CHO KHÁCH VÃNG LAI
   ============================================================ */

let checkStatusInterval = null;

// Utils format tiền (nếu chưa có trong file common)
function formatPaymentPrice(price) {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
}

/**
 * 1. RENDER GIAO DIỆN THANH TOÁN
 */
async function renderGuestPayment() {
    const el = document.getElementById("payment-content");
    if (!el) return;

    // Kiểm tra bàn
    if (!TABLE_ID) {
        el.innerHTML = '<div class="payment-empty">Lỗi: Mất thông tin bàn. Quét lại QR.</div>';
        return;
    }

    // Loading...
    el.innerHTML = '<div style="text-align:center; padding:20px; color:#888">⏳ Đang tải thông tin...</div>';

    try {
        // GỌI API GUEST: Lấy hóa đơn theo bàn
        const response = await fetch(`/api/guest/invoice/${TABLE_ID}`);

        if (response.ok) {
            const orders = await response.json();

            // Nếu không có đơn hàng nào hoặc tất cả đã hủy
            if (!orders || orders.length === 0) {
                el.innerHTML = `
                    <div class="payment-empty">
                        Không có đơn hàng để thanh toán
                    </div>`;
                return;
            }

            // Tính tổng tiền từ dữ liệu API
            // Chỉ tính những đơn chưa bị Hủy
            const validOrders = orders.filter(o => o.status !== 'CANCELLED');
            const total = validOrders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);

            if (total === 0) {
                el.innerHTML = `<div class="payment-empty">Đơn hàng đã thanh toán hết.</div>`;
                return;
            }

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
                    
                    <div id="qr-container" style="display:none; text-align:center; margin:15px 0;">
                        <img src="https://img.vietqr.io/image/MB-000000000-compact2.jpg?amount=${total}&addInfo=BAN ${TABLE_ID} THANH TOAN" 
                             alt="QR Chuyen Khoan" style="max-width:200px; border-radius:8px;">
                        <p style="font-size:0.9em; color:#666;">Quét mã để thanh toán</p>
                    </div>

                    <button class="btn btn-primary btn-full" 
                        style="width:100%; padding:15px; border-radius:12px; background:var(--color-secondary); color:white; font-weight:bold; border:none; font-size:1rem; cursor:pointer;"
                        onclick="confirmGuestPayment(${total})">
                        Yêu cầu thanh toán
                    </button>
                </div>
            `;

            // Logic hiện QR code khi chọn radio button
            document.querySelectorAll('input[name="paymentMethod"]').forEach(input => {
                input.addEventListener('change', (e) => {
                    const qrDiv = document.getElementById('qr-container');
                    if(qrDiv) qrDiv.style.display = e.target.value === 'TRANSFER' ? 'block' : 'none';
                });
            });

        } else {
            el.innerHTML = '<div class="payment-empty">Lỗi tải dữ liệu hóa đơn</div>';
        }
    } catch (e) {
        console.error(e);
        el.innerHTML = '<div class="payment-empty">Lỗi kết nối máy chủ</div>';
    }

    // Kiểm tra xem có đang chờ thu ngân không (để hiện màn hình chờ)
    checkIfWaitingForCashier();
}

/**
 * 2. KIỂM TRA TRẠNG THÁI CHỜ
 */
async function checkIfWaitingForCashier() {
    const el = document.getElementById("payment-content");
    if (!TABLE_ID) return;

    try {
        // GỌI API GUEST
        const response = await fetch(`/api/guest/invoice/${TABLE_ID}`);
        if (response.ok) {
            const orders = await response.json();

            // Tìm xem có đơn nào đang PAYMENT_PENDING không
            const isPending = orders.some(o => o.status === 'PAYMENT_PENDING');

            if (isPending) {
                showWaitingScreen(el);
                startPollingStatus(); // Bắt đầu loop kiểm tra
            }
        }
    } catch (e) { console.error(e); }
}

/**
 * 3. GỬI YÊU CẦU THANH TOÁN
 */
async function confirmGuestPayment(totalAmount) {
    const methodInput = document.querySelector('input[name="paymentMethod"]:checked');
    const method = methodInput ? methodInput.value : "CASH";

    if (!confirm("Xác nhận gửi yêu cầu thanh toán đến thu ngân?")) return;

    try {
        // Payload gửi lên API Guest
        const payload = {
            tableId: parseInt(TABLE_ID), // BẮT BUỘC: Guest cần ID bàn
            method: method
        };

        // GỌI API GUEST CHECKOUT
        const response = await fetch('/api/guest/payment/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            // Hiện màn hình chờ
            const el = document.getElementById("payment-content");
            showWaitingScreen(el);

            // Bắt đầu lắng nghe
            startPollingStatus();
        } else {
            const msg = await response.text();
            alert("Lỗi: " + msg);
        }
    } catch (e) {
        alert("Lỗi kết nối mạng!");
    }
}

// 4. MÀN HÌNH CHỜ THU NGÂN
function showWaitingScreen(container) {
    if(!container) return;
    container.innerHTML = `
        <div class="payment-card" style="text-align:center; padding: 40px 20px;">
            <div class="spinner-border text-warning" style="width: 3rem; height: 3rem; margin-bottom: 20px;" role="status"></div>
            <h3 style="color: #d35400; margin-bottom: 10px;">Đã gọi nhân viên!</h3>
            <p style="color: #666; margin-bottom: 20px;">
                Vui lòng đợi thu ngân xác nhận thanh toán.<br>
                Trạng thái sẽ tự động cập nhật...
            </p>
            <button class="btn" style="background:#eee; color:#666; border:none; padding:10px 20px; border-radius:8px;" onclick="renderGuestPayment()">Làm mới</button>
        </div>
    `;
}

// 5. CƠ CHẾ POLLING (TỰ ĐỘNG KIỂM TRA)
function startPollingStatus() {
    if (checkStatusInterval) clearInterval(checkStatusInterval);

    console.log("Bắt đầu theo dõi trạng thái thanh toán...");

    checkStatusInterval = setInterval(async () => {
        try {
            // API trả về danh sách các đơn CHƯA hoàn tất quy trình
            const response = await fetch(`/api/guest/invoice/${TABLE_ID}`);

            if (response.ok) {
                const orders = await response.json();

                // Logic hoàn tất:
                // 1. Danh sách rỗng (đã PAID hết và server không trả về nữa)
                // 2. Hoặc tất cả đơn trong danh sách đều đã PAID (nếu server vẫn trả về đơn PAID)
                // 3. Hoặc đơn giản là không còn đơn nào PAYMENT_PENDING hay PENDING/COOKING

                // Ở đây giả định API /guest/invoice chỉ trả về đơn đang phục vụ.
                // Nếu trả về rỗng => Đã thanh toán xong hết.
                if (!orders || orders.length === 0) {
                    clearInterval(checkStatusInterval);
                    alert("Thanh toán thành công! Cảm ơn quý khách.");

                    // Reset trạng thái bàn cho khách mới hoặc reload
                    localStorage.removeItem(Storage.getCartKey()); // Xóa giỏ hàng thừa
                    window.location.reload(); // Tải lại trang để về trạng thái ban đầu
                }
            }
        } catch (e) {
            console.error("Lỗi kiểm tra trạng thái", e);
        }
    }, 3000); // Check mỗi 3 giây
}

// Đăng ký sự kiện click tab Payment
document
    .querySelector('[data-tab="payment"]')
    ?.addEventListener("click", renderGuestPayment);