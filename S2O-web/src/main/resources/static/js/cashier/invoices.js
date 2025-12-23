/* invoices.js */

// Dữ liệu giả lập
const invoicesData = [
    {
        id: 'INV-001', table: 2, status: 'unpaid', time: '12:30 20/12/2025',
        items: [{name: 'Phở Bò', price: 65000, qty: 2}, {name: 'Quẩy', price: 5000, qty: 4}],
        total: 150000
    },
    {
        id: 'INV-002', table: 5, status: 'paid', method: 'Tiền mặt', time: '11:15 20/12/2025',
        items: [{name: 'Bún Chả', price: 55000, qty: 2}, {name: 'Nem Cua', price: 20000, qty: 2}],
        total: 150000
    },
    {
        id: 'INV-003', table: 8, status: 'unpaid', time: '12:45 20/12/2025',
        items: [{name: 'Lẩu Thái', price: 350000, qty: 1}, {name: 'Pepsi', price: 15000, qty: 4}],
        total: 410000
    },
    {
        id: 'INV-004', table: 1, status: 'paid', method: 'Chuyển khoản', time: '10:30 20/12/2025',
        items: [{name: 'Cafe Sữa', price: 25000, qty: 2}],
        total: 50000
    },
];

let currentFilter = 'unpaid'; // Mặc định hiển thị tab Chờ thanh toán

document.addEventListener("DOMContentLoaded", () => {
    updateStats();
    filterInvoices('unpaid'); // Mặc định load tab chưa thanh toán
});

/* --- 1. RENDER LIST --- */
function filterInvoices(status) {
    currentFilter = status;

    // Update UI Tabs
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    // Tìm nút tương ứng để active (cách đơn giản dựa trên text hoặc onclick)
    const btns = document.querySelectorAll('.filter-btn');
    if(status === 'unpaid') btns[0].classList.add('active');
    else btns[1].classList.add('active');

    // Filter Data
    const filtered = invoicesData.filter(inv => inv.status === status);
    const container = document.getElementById("invoice-list");

    if (filtered.length === 0) {
        container.innerHTML = `<div style="text-align:center; color:#999; padding:2rem;">Không có hóa đơn nào</div>`;
        return;
    }

    container.innerHTML = filtered.map(inv => {
        const badgeClass = inv.status === 'unpaid' ? 'unpaid' : 'paid';
        const badgeText = inv.status === 'unpaid' ? 'Chờ thanh toán' : 'Đã thanh toán';
        const methodText = inv.status === 'paid' ? `<span style="font-size:0.8rem; color:#00b894; margin-left:10px;">(${inv.method})</span>` : '';

        return `
            <div class="invoice-card">
                <div class="inv-left">
                    <div class="inv-icon"><i class="fa-solid fa-receipt"></i></div>
                    <div class="inv-details">
                        <h4>${inv.id} <span class="inv-badge ${badgeClass}">${badgeText}</span> ${methodText}</h4>
                        <p>
                            <span><i class="fa-solid fa-table"></i> Bàn ${inv.table}</span>
                            <span><i class="fa-regular fa-clock"></i> ${inv.time}</span>
                            <span><i class="fa-solid fa-layer-group"></i> ${inv.items.length} món</span>
                        </p>
                    </div>
                </div>
                <div class="inv-right">
                    <span class="inv-total">${inv.total.toLocaleString()}đ</span>
                    <button class="btn-view" onclick="openInvoiceDetail('${inv.id}')">
                        <i class="fa-regular fa-eye"></i> Xem
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

/* --- 2. STATS --- */
function updateStats() {
    // Chỉ tính doanh thu của hóa đơn đã thanh toán (paid)
    const paidInvoices = invoicesData.filter(i => i.status === 'paid');
    const totalRevenue = paidInvoices.reduce((sum, item) => sum + item.total, 0);
    const avg = paidInvoices.length ? totalRevenue / paidInvoices.length : 0;

    document.getElementById('stat-count').innerText = invoicesData.length; // Tổng all hóa đơn trong ngày
    document.getElementById('stat-revenue').innerText = totalRevenue.toLocaleString() + 'đ';
    document.getElementById('stat-avg').innerText = avg.toLocaleString() + 'đ';
}

/* --- 3. MODAL & LOGIC NÚT BẤM --- */
function openInvoiceDetail(id) {
    const inv = invoicesData.find(i => i.id === id);
    if(!inv) return;

    // Fill thông tin cơ bản
    document.getElementById('m-inv-id').innerText = inv.id;
    document.getElementById('m-table').innerText = `#${inv.table}`;
    document.getElementById('m-time').innerText = inv.time;
    document.getElementById('m-total').innerText = inv.total.toLocaleString() + 'đ';

    // Render món ăn
    const listHtml = inv.items.map(item => `
        <li>
            <span class="item-name"><span class="item-qty">${item.qty}x</span> ${item.name}</span>
            <span class="item-price">${(item.price * item.qty).toLocaleString()}đ</span>
        </li>
    `).join('');
    document.getElementById('m-items-list').innerHTML = listHtml;

    // Xử lý logic hiển thị theo trạng thái (QUAN TRỌNG)
    const actionContainer = document.getElementById('m-actions');
    const paymentInfoBox = document.getElementById('m-payment-info');

    if (inv.status === 'unpaid') {
        // TRƯỜNG HỢP: CHƯA THANH TOÁN
        paymentInfoBox.style.display = 'none'; // Ẩn thông tin phương thức

        // Hiện 2 nút: In và Thanh Toán
        actionContainer.innerHTML = `
            <button class="btn-print" onclick="printInvoice('${inv.id}')">
                <i class="fa-solid fa-print"></i> In Hóa Đơn
            </button>
            <button class="btn-pay" onclick="goToPayment('${inv.id}')">
                <i class="fa-solid fa-credit-card"></i> Thanh Toán
            </button>
        `;
    } else {
        // TRƯỜNG HỢP: ĐÃ THANH TOÁN
        paymentInfoBox.style.display = 'block';
        document.getElementById('m-method').innerText = inv.method;

        // Chỉ hiện nút In (hoặc Tải về)
        actionContainer.innerHTML = `
             <button class="btn-print" style="width:100%" onclick="printInvoice('${inv.id}')">
                <i class="fa-solid fa-print"></i> In Lại Hóa Đơn
            </button>
        `;
    }

    document.getElementById('invoice-modal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('invoice-modal').style.display = 'none';
}

// Click ngoài modal để đóng
window.onclick = function(e) {
    if (e.target == document.getElementById('invoice-modal')) {
        closeModal();
    }
}

/* --- 4. CHỨC NĂNG THANH TOÁN & IN --- */

function printInvoice(id) {
    alert(`Đang in hóa đơn ${id}... (Mô phỏng)`);
}

function goToPayment(id) {
    // 1. Tìm hóa đơn
    const inv = invoicesData.find(i => i.id === id);

    // 2. Lưu thông tin hóa đơn này vào LocalStorage để trang payment.html đọc được
    // Ta gửi toàn bộ object invoice qua
    localStorage.setItem('pendingInvoice', JSON.stringify(inv));

    // 3. Chuyển trang
    window.location.href = 'payment.html';
}