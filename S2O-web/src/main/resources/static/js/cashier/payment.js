/* ================= CONFIG ================= */
const API_ENDPOINTS = {
    GET_ORDER_DETAIL: id => `/api/cashier/orders/${id}`,
    PROCESS_PAYMENT: '/api/cashier/payment/process'
};

/* ================= STATE ================= */
const state = {
    orderId: null,
    tableId: null,
    totalAmount: 0,
    currentPaymentMethod: 'CASH',
    cashGiven: 0,
    bankConfig: null
};

/* ================= INIT ================= */
document.addEventListener('DOMContentLoaded', async () => {

    if (typeof renderTopHeader === 'function') renderTopHeader();
    if (typeof renderCashierMenu === 'function') renderCashierMenu('payment');

    const params = new URLSearchParams(window.location.search);
    const orderId = params.get('orderId');
    const tableId = params.get('tableId');

    try {
        if (tableId) {
            state.tableId = parseInt(tableId);
            await loadOrdersByTable(state.tableId);
        } else if (orderId) {
            state.orderId = parseInt(orderId);
            await loadOrderDetails(state.orderId);
        } else {
            alert("Không tìm thấy thông tin thanh toán");
            return;
        }
    } catch (e) {
        console.error(e);
        alert("Lỗi tải dữ liệu thanh toán");
    }

    updateCashDisplayOnly();
});

/* ================= LOAD DATA ================= */
async function loadOrderDetails(id) {
    const res = await fetch(API_ENDPOINTS.GET_ORDER_DETAIL(id));
    if (!res.ok) throw new Error("Load order failed");

    const data = await res.json();

    state.totalAmount = data.totalAmount;
    state.bankConfig = data.bankConfig || null;

    renderHeader(data.tableName || 'Mang về');
    renderItems(data.items);
    updateSummary();

    updateQrCode(state.totalAmount, `TT Don ${id}`);
}

async function loadOrdersByTable(tableId) {
    const res = await fetch('/api/cashier/invoices');
    if (!res.ok) throw new Error("Load invoices failed");

    const invoices = await res.json();
    const orders = invoices.filter(
        o => o.status === 'UNPAID' && o.tableId === tableId
    );

    if (!orders.length) {
        alert("Bàn này không có hóa đơn chưa thanh toán");
        return;
    }

    /* ===== FIX QUAN TRỌNG: LẤY BANK CONFIG ===== */
    const firstOrderId = orders[0].orderId;
    if (firstOrderId) {
        try {
            const orderRes = await fetch(API_ENDPOINTS.GET_ORDER_DETAIL(firstOrderId));
            if (orderRes.ok) {
                const orderData = await orderRes.json();
                state.bankConfig = orderData.bankConfig || null;
            }
        } catch (e) {
            console.warn("Không lấy được bankConfig", e);
        }
    }
    /* ========================================== */

    state.totalAmount = orders.reduce((sum, o) => sum + o.total, 0);

    renderHeader(`Bàn ${orders[0].table}`);
    renderItems(
        orders.flatMap(o =>
            o.items.map(i => ({
                productName: i.name,
                quantity: i.qty,
                unitPrice: i.price
            }))
        )
    );

    updateSummary();
    updateQrCode(state.totalAmount, `TT Ban ${tableId}`);
}

/* ================= UI ================= */
function renderHeader(name) {
    const el = document.getElementById('display-table-id');
    if (el) el.innerText = name;
}

function renderItems(items) {
    const list = document.getElementById('bill-items-list');
    if (!list) return;

    list.innerHTML = '';
    items.forEach(i => {
        list.innerHTML += `
        <li class="bill-item">
            <div class="item-info">
                <span class="item-name">${i.productName}</span>
                <span class="item-qty">x${i.quantity}</span>
            </div>
            <span class="item-price">${formatCurrency(i.unitPrice * i.quantity)}</span>
        </li>`;
    });
}

function updateSummary() {
    document.getElementById('bill-subtotal').innerText = formatCurrency(state.totalAmount);
    document.getElementById('bill-final-total').innerText = formatCurrency(state.totalAmount);
    document.getElementById('btn-pay-amount').innerText = formatCurrency(state.totalAmount);
}

/* ================= PAYMENT UI ================= */
function switchMethod(method) {
    document.querySelectorAll('.m-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(`btn-${method}`)?.classList.add('active');

    document.querySelectorAll('.payment-content').forEach(c => c.style.display = 'none');
    const content = document.getElementById(`content-${method}`);
    if (content) content.style.display = 'block';

    state.currentPaymentMethod =
        method === 'cash' ? 'CASH' :
            method === 'qr' ? 'BANK_TRANSFER' : 'E_WALLET';

    if (method === 'qr') {
        updateQrCode(state.totalAmount, `TT ${state.tableId || state.orderId}`);
    }
}

/* ================= CASH ================= */
function addCash(amount) {
    state.cashGiven += amount;
    updateCashDisplayOnly();
}

function resetCash() {
    state.cashGiven = 0;
    updateCashDisplayOnly();
}

function calculateChange() {
    const v = document.getElementById('cash-given')?.value;
    state.cashGiven = v ? parseFloat(v) : 0;
    updateCashDisplayOnly();
}

function updateCashDisplayOnly() {
    const change = state.cashGiven - state.totalAmount;
    const el = document.getElementById('cash-return');
    if (!el) return;

    if (change >= 0) {
        el.innerText = formatCurrency(change);
        el.style.color = 'green';
    } else {
        el.innerText = `Thiếu ${formatCurrency(-change)}`;
        el.style.color = 'red';
    }
}

/* ================= QR ================= */
function updateQrCode(amount, desc) {

    const amountText = document.getElementById('qr-display-amount');
    if (amountText) {
        amountText.innerText = formatCurrency(amount);
    }

    if (!state.bankConfig) {
        console.warn("Chưa có cấu hình ngân hàng");
        return;
    }

    const { bankId, accountNo, template, accountName } = state.bankConfig;
    const img = document.querySelector('.qr-container img');
    if (!img) return;

    img.src =
        `https://img.vietqr.io/image/${bankId}-${accountNo}-${template || 'compact2'}.png` +
        `?amount=${amount}` +
        `&addInfo=${encodeURIComponent(desc)}` +
        `&accountName=${encodeURIComponent(accountName)}`;
}

/* ================= SUBMIT ================= */
async function processPayment() {

    if (state.currentPaymentMethod === 'CASH' &&
        state.cashGiven < state.totalAmount) {
        alert("Tiền khách đưa chưa đủ");
        return;
    }

    if (!confirm(`Xác nhận thanh toán ${formatCurrency(state.totalAmount)}?`)) return;

    const payload = {
        paymentMethod: state.currentPaymentMethod,
        transactionRef: generateTransactionRef(state.currentPaymentMethod)
    };

    if (state.tableId) {
        payload.tableId = state.tableId;
    } else {
        payload.orderId = state.orderId;
        payload.amountPaid = state.totalAmount;
    }

    const res = await fetch(API_ENDPOINTS.PROCESS_PAYMENT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert("Thanh toán thành công");
        window.location.href = '/cashier/tables';
    } else {
        const e = await res.json();
        alert(e.message || "Thanh toán thất bại");
    }
}

/* ================= HELPERS ================= */
function formatCurrency(v) {
    return new Intl.NumberFormat('vi-VN',
        { style: 'currency', currency: 'VND' }).format(v);
}

function generateTransactionRef(m) {
    const t = Date.now().toString().slice(-6);
    return m === 'CASH' ? `CASH-${t}` :
        m === 'BANK_TRANSFER' ? `QR-${t}` : `CARD-${t}`;
}
