// Biến lưu thông tin hóa đơn hiện tại
let currentInvoice = null;
let currentMethod = 'cash'; // Mặc định

document.addEventListener("DOMContentLoaded", () => {
    loadInvoiceData();
});

function loadInvoiceData() {
    // 1. Lấy dữ liệu từ LocalStorage (được gửi từ invoices.html)
    const storedData = localStorage.getItem('pendingInvoice');

    if (storedData) {
        currentInvoice = JSON.parse(storedData);
    } else {
        // Dữ liệu giả lập nếu test trực tiếp file này (Không qua invoices)
        currentInvoice = {
            id: 'TEST-DIRECT', table: 5, total: 165000,
            items: [
                {name: 'Phở Bò', qty: 2, price: 65000},
                {name: 'Gỏi Cuốn', qty: 1, price: 35000}
            ]
        };
    }

    renderInvoiceUI();
}

function renderInvoiceUI() {
    if(!currentInvoice) return;

    // Render thông tin bàn
    document.getElementById('display-table-id').innerText = `Bàn #${currentInvoice.table} - ${currentInvoice.id}`;

    // Render list món ăn
    const itemsHtml = currentInvoice.items.map(item => `
        <li>
            <span class="qty">${item.qty}x</span>
            <span class="name">${item.name}</span>
            <span class="price">${(item.price * item.qty).toLocaleString()}đ</span>
        </li>
    `).join('');
    document.getElementById('bill-items-list').innerHTML = itemsHtml;

    // Hiển thị tổng tiền
    const total = currentInvoice.total;
    document.getElementById('bill-subtotal').innerText = total.toLocaleString() + 'đ';
    document.getElementById('bill-final-total').innerText = total.toLocaleString() + 'đ';
    document.getElementById('btn-pay-amount').innerText = total.toLocaleString() + 'đ';

    // Cập nhật số liệu hiển thị bên phải
    document.getElementById('cash-must-pay').innerText = total.toLocaleString() + 'đ';
    document.getElementById('qr-display-amount').innerText = total.toLocaleString() + 'đ';
}

/* --- CHUYỂN ĐỔI TAB THANH TOÁN --- */
function switchMethod(method) {
    currentMethod = method;

    // 1. Active Buttons
    document.querySelectorAll('.m-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById(`btn-${method}`).classList.add('active');

    // 2. Show Content
    document.querySelectorAll('.payment-content').forEach(content => content.style.display = 'none');
    document.getElementById(`content-${method}`).style.display = 'block';

    // Đổi màu nút xác nhận
    const confirmBtn = document.querySelector('.confirm-pay-btn');
    if (method === 'card') confirmBtn.style.background = '#e67e22';
    else if (method === 'qr') confirmBtn.style.background = '#2E25D1';
    else confirmBtn.style.background = '#2d3436';
}

/* --- LOGIC TIỀN MẶT (CỘNG DỒN) --- */
function addCash(amount) {
    const inputEl = document.getElementById('cash-given');

    // Lấy giá trị hiện tại, nếu rỗng thì là 0
    let currentVal = parseInt(inputEl.value);
    if (isNaN(currentVal)) currentVal = 0;

    // Cộng dồn
    const newVal = currentVal + amount;
    inputEl.value = newVal;

    // Tính lại tiền thừa
    calculateChange();
}

function resetCash() {
    const inputEl = document.getElementById('cash-given');
    inputEl.value = ""; // Xóa trắng

    // Reset hiển thị
    const returnEl = document.getElementById('cash-return');
    returnEl.innerText = "0đ";
    returnEl.style.color = "#2d3436";

    inputEl.focus();
}

function calculateChange() {
    const given = parseInt(document.getElementById('cash-given').value) || 0;

    if (!currentInvoice) return;
    const total = currentInvoice.total;

    const returnAmount = given - total;
    const returnEl = document.getElementById('cash-return');

    if (returnAmount >= 0) {
        returnEl.innerText = returnAmount.toLocaleString() + 'đ';
        returnEl.style.color = '#00b894'; // Xanh
    } else {
        returnEl.innerText = "Thiếu " + Math.abs(returnAmount).toLocaleString() + 'đ';
        returnEl.style.color = '#d63031'; // Đỏ
    }
}

/* --- XỬ LÝ THANH TOÁN --- */
function processPayment() {
    if (currentMethod === 'cash') {
        const given = parseInt(document.getElementById('cash-given').value) || 0;
        if (given < currentInvoice.total) {
            alert("Số tiền khách đưa chưa đủ!");
            return;
        }
    }

    if(confirm(`Xác nhận thanh toán ${currentInvoice.total.toLocaleString()}đ?`)) {
        alert("Thanh toán thành công!");

        // Clear data và về trang chủ
        localStorage.removeItem('pendingInvoice');
        window.location.href = 'invoices.html';
    }
}