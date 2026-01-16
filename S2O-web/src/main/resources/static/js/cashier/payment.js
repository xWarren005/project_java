/* ==========================================================================
   CONSTANTS & CONFIG
   ========================================================================== */
const API_ENDPOINTS = {
    // API lấy chi tiết đơn hàng
    GET_ORDER_DETAIL: (id) => `/api/cashier/orders/${id}`,
    // API xử lý thanh toán
    PROCESS_PAYMENT: '/api/cashier/payment/process'
};

/* ==========================================================================
   STATE MANAGEMENT
   ========================================================================== */
const state = {
    orderId: null,
    totalAmount: 0,
    currentPaymentMethod: 'CASH', // Enum: CASH, BANK_TRANSFER, E_WALLET
    cashGiven: 0,

    // [THÊM] Biến lưu cấu hình ngân hàng lấy từ API
    bankConfig: null
};

/* ==========================================================================
   INIT
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {

    if(typeof renderTopHeader === 'function') renderTopHeader();
    if(typeof renderCashierMenu === 'function') renderCashierMenu('payment');

    // 1. Lấy orderId từ URL (Ví dụ: /cashier/payment?orderId=1001)
    const urlParams = new URLSearchParams(window.location.search);
    const orderIdParam = urlParams.get('orderId');

    if (orderIdParam) {
        state.orderId = parseInt(orderIdParam);
        loadOrderDetails(state.orderId);
    } else {
        alert("Không tìm thấy mã đơn hàng!");
    }

    // 2. Format tiền tệ ban đầu
    updateMoneyDisplay();
});

/* ==========================================================================
   LOGIC: LOAD DATA & RENDER
   ========================================================================== */
async function loadOrderDetails(id) {
    try {
        const response = await fetch(API_ENDPOINTS.GET_ORDER_DETAIL(id));

        if (!response.ok) {
            throw new Error(`Lỗi kết nối Server: ${response.status}`);
        }

        const data = await response.json();

        // [MỚI] Lưu cấu hình ngân hàng từ Backend vào State
        if (data.bankConfig) {
            state.bankConfig = data.bankConfig;
        } else {
            console.warn("Không tìm thấy cấu hình QR Ngân hàng trong đơn hàng này.");
        }

        // Render tên bàn
        document.getElementById('display-table-id').innerText = data.tableName || 'Mang về';

        // Render danh sách món
        const itemsList = document.getElementById('bill-items-list');
        itemsList.innerHTML = '';

        if (data.items && data.items.length > 0) {
            data.items.forEach(item => {
                const li = document.createElement('li');
                li.className = 'bill-item';
                li.innerHTML = `
                    <div class="item-info">
                        <span class="item-name">${item.productName}</span>
                        <span class="item-qty">x${item.quantity}</span>
                    </div>
                    <span class="item-price">${formatCurrency(item.unitPrice * item.quantity)}</span>
                `;
                itemsList.appendChild(li);
            });
        }

        // Cập nhật State Amount
        state.totalAmount = data.totalAmount;

        // Cập nhật UI tổng tiền
        document.getElementById('bill-subtotal').innerText = formatCurrency(state.totalAmount);
        document.getElementById('bill-final-total').innerText = formatCurrency(state.totalAmount);
        document.getElementById('btn-pay-amount').innerText = formatCurrency(state.totalAmount);

        // Cập nhật QR Code (Hàm này sẽ tự check xem có bankConfig chưa)
        updateQrCode(state.totalAmount, `TT Don hang ${state.orderId}`);

    } catch (error) {
        console.error("Lỗi tải đơn hàng:", error);
        alert("Không thể tải thông tin đơn hàng. Vui lòng kiểm tra lại ID hoặc Server.");
    }
}

/* ==========================================================================
   LOGIC: UI INTERACTION
   ========================================================================== */

function switchMethod(method) {
    // 1. Reset active buttons
    document.querySelectorAll('.m-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(`btn-${method}`).classList.add('active');

    // 2. Ẩn hiện nội dung
    document.querySelectorAll('.payment-content').forEach(content => {
        content.style.display = 'none';
        content.classList.remove('active');
    });

    const contentId = `content-${method}`;
    const activeContent = document.getElementById(contentId);
    if(activeContent) {
        activeContent.style.display = 'block';
        setTimeout(() => activeContent.classList.add('active'), 10);
    }

    // 3. Cập nhật State
    switch (method) {
        case 'cash':
            state.currentPaymentMethod = 'CASH';
            break;
        case 'card':
            state.currentPaymentMethod = 'E_WALLET';
            break;
        case 'qr':
            state.currentPaymentMethod = 'BANK_TRANSFER';
            // Khi chuyển sang Tab QR, kiểm tra xem có config chưa để cảnh báo
            if (!state.bankConfig) {
                console.warn("Chưa có thông tin tài khoản ngân hàng để tạo QR.");
                document.querySelector('.qr-note').innerHTML = '<span style="color:red"><i class="fa-solid fa-triangle-exclamation"></i> Nhà hàng chưa cấu hình tài khoản ngân hàng.</span>';
            }
            break;
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

/* ==========================================================================
   LOGIC: PAYMENT METHODS
   ========================================================================== */

/* --- 1. TIỀN MẶT (CASH) --- */
function addCash(amount) {
    state.cashGiven += amount;
    updateCashUI();
}

function resetCash() {
    state.cashGiven = 0;
    updateCashUI();
}

function calculateChange() {
    const inputVal = document.getElementById('cash-given').value;
    state.cashGiven = inputVal ? parseFloat(inputVal) : 0;
    updateCashDisplayOnly();
}

function updateCashUI() {
    document.getElementById('cash-given').value = state.cashGiven > 0 ? state.cashGiven : '';
    updateCashDisplayOnly();
}

function updateCashDisplayOnly() {
    const mustPay = state.totalAmount;
    const change = state.cashGiven - mustPay;

    document.getElementById('cash-must-pay').innerText = formatCurrency(mustPay);

    const changeEl = document.getElementById('cash-return');
    if (change >= 0) {
        changeEl.innerText = formatCurrency(change);
        changeEl.style.color = 'var(--success-color)';
    } else {
        changeEl.innerText = "Thiếu " + formatCurrency(Math.abs(change));
        changeEl.style.color = 'var(--danger-color)';
    }
}

/* --- 2. QR CODE (BANK TRANSFER) --- */
function updateQrCode(amount, description) {
    const qrImg = document.querySelector('.qr-container img');
    const qrDisplay = document.getElementById('qr-display-amount');

    // Hiển thị số tiền
    if(qrDisplay) {
        qrDisplay.innerText = formatCurrency(amount);
    }

    // [QUAN TRỌNG] Kiểm tra xem đã có thông tin ngân hàng từ API chưa
    if (!state.bankConfig || !state.bankConfig.bankId || !state.bankConfig.accountNo) {
        // Nếu chưa có config, hiển thị ảnh lỗi hoặc giữ nguyên placeholder
        if(qrImg) qrImg.alt = "Chưa có thông tin ngân hàng";
        return;
    }

    // Lấy thông tin từ state
    const { bankId, accountNo, template, accountName } = state.bankConfig;

    // Sử dụng template mặc định nếu thiếu
    const templateUsed = template || "compact2";

    // Tạo URL VietQR động
    const qrUrl = `https://img.vietqr.io/image/${bankId}-${accountNo}-${templateUsed}.png?amount=${amount}&addInfo=${encodeURIComponent(description)}&accountName=${encodeURIComponent(accountName)}`;

    if (qrImg) {
        qrImg.src = qrUrl;
    }
}

/* ==========================================================================
   LOGIC: SUBMIT PAYMENT
   ========================================================================== */
async function processPayment() {
    if (state.currentPaymentMethod === 'CASH') {
        if (state.cashGiven < state.totalAmount) {
            alert("Tiền khách đưa chưa đủ!");
            return;
        }
    }

    const confirmMsg = `Xác nhận thanh toán ${formatCurrency(state.totalAmount)} bằng ${getMethodName(state.currentPaymentMethod)}?`;
    if (!confirm(confirmMsg)) return;

    const payload = {
        orderId: state.orderId,
        paymentMethod: state.currentPaymentMethod,
        amountPaid: state.totalAmount,
        transactionRef: generateTransactionRef(state.currentPaymentMethod)
    };

    try {
        const response = await fetch(API_ENDPOINTS.PROCESS_PAYMENT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok) {
            alert("Thanh toán thành công!");
            window.location.href = '/cashier/tables';
        } else {
            alert("Lỗi: " + (result.message || "Thanh toán thất bại"));
        }
    } catch (error) {
        console.error("Payment Error:", error);
        alert("Lỗi kết nối đến máy chủ.");
    }
}

function getMethodName(code) {
    if (code === 'CASH') return "Tiền mặt";
    if (code === 'BANK_TRANSFER') return "Chuyển khoản QR";
    return "Thẻ/Ví điện tử";
}

function generateTransactionRef(method) {
    const timestamp = Date.now().toString().slice(-6);
    if (method === 'CASH') return `CASH-${timestamp}`;
    if (method === 'BANK_TRANSFER') return `QR-${timestamp}`;
    return `CARD-${timestamp}`;
}

function updateMoneyDisplay() {
    updateCashDisplayOnly();
}