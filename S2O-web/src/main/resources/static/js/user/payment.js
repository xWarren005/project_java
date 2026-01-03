/* ============================================================
   USER PAYMENT JS - THANH TOÁN CHO KHÁCH HÀNG (User Version)
   ============================================================ */

let checkStatusInterval = null;

// Utils format tiền
function formatPaymentPrice(price) {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
}

/**
 * 1. RENDER GIAO DIỆN THANH TOÁN
 */
async function renderPayment() {
    const el = document.getElementById("payment-content");
    if (!el) return;

    // Loading...
    el.innerHTML = '<div style="text-align:center; padding:20px; color:#888">⏳ Đang tải thông tin...</div>';

    try {
        const response = await fetch('/api/user/invoice/current?v=' + new Date().getTime(), {
            headers: { 'Cache-Control': 'no-cache' }
        });

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
                    <button class="btn btn-primary btn-full" 
                        style="width:100%; padding:15px; border-radius:12px; background:var(--color-secondary); color:white; font-weight:bold; border:none; font-size:1rem; cursor:pointer;"
                        onclick="confirmPayment(${total})">
                        Yêu cầu thanh toán
                    </button>
                </div>
            `;

        } else {
            el.innerHTML = '<div class="payment-empty">Lỗi tải dữ liệu hóa đơn</div>';
        }
    } catch (e) {
        console.error(e);
        el.innerHTML = '<div class="payment-empty">Lỗi kết nối máy chủ</div>';
    }

}

/**
 * 3. GỬI YÊU CẦU THANH TOÁN
 */
async function confirmPayment(totalAmount) {
    const methodInput = document.querySelector('input[name="paymentMethod"]:checked');
    const method = methodInput ? methodInput.value : "CASH";

    try {
        const payload = {
            method: method
        };

        // GỌI API USER CHECKOUT
        const response = await fetch('/api/user/payment/checkout', {
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

            if (response.status === 400 || msg.includes("đã gửi")) {
                const el = document.getElementById("payment-content");
                showWaitingScreen(el);
                startPollingStatus();
            } else {
                alert("Lỗi: " + msg);
            }
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
            </p>
            <button class="btn" style="background:#eee; color:#666; border:none; padding:10px 20px; border-radius:8px;" onclick="renderPayment()">Làm mới</button>
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
            // Thêm chống cache
            const response = await fetch('/api/user/invoice/current?v=' + new Date().getTime());

            if (response.ok) {
                const orders = await response.json();

                // Logic hoàn tất:
                // 1. Danh sách rỗng (đã PAID hết và server không trả về nữa)
                // 2. Hoặc tất cả đơn trong danh sách đều đã PAID
                if (!orders || orders.length === 0) {
                    clearInterval(checkStatusInterval);
                    alert("Thanh toán thành công! Cảm ơn quý khách.");

                    // User: Chuyển hướng sang lịch sử
                    window.location.reload();
                }
            }
        } catch (e) {
            console.error("Lỗi kiểm tra trạng thái", e);
        }
    }, 3000); // Check mỗi 3 giây
}