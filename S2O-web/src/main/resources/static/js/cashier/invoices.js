/* invoices.js */

// Biến lưu trữ dữ liệu toàn cục
let invoicesData = [];
// SỬA: Mặc định filter là UNPAID (in hoa)
let currentFilter = 'UNPAID';

document.addEventListener("DOMContentLoaded", () => {
    // Menu cashier
    if(typeof renderTopHeader === 'function') renderTopHeader();
    if(typeof renderCashierMenu === 'function') renderCashierMenu('invoices');

    fetchInvoices();
    setInterval(fetchInvoices, 30000);
});

/* --- 0. FETCH DATA FROM API --- */
async function fetchInvoices() {
    try {
        const response = await fetch('/api/cashier/invoices');
        if (!response.ok) throw new Error('Network response was not ok');

        invoicesData = await response.json();
        updateStats();
        filterInvoices(currentFilter);
    } catch (error) {
        console.error('Error fetching invoices:', error);
    }
}

/* --- 1. RENDER LIST --- */
function filterInvoices(status) {
    currentFilter = status;

    // Update UI Tabs
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    const btns = document.querySelectorAll('.filter-btn');

    // SỬA: Check active theo UNPAID/PAID
    if(status === 'UNPAID') btns[0].classList.add('active');
    else btns[1].classList.add('active');

    // Filter Data
    const filtered = invoicesData.filter(inv => inv.status === status);
    const container = document.getElementById("invoice-list");

    if (filtered.length === 0) {
        container.innerHTML = `<div style="text-align:center; color:#999; padding:3rem;">
            <i class="fa-solid fa-clipboard-list" style="font-size:2rem; margin-bottom:10px; display:block"></i>
            Không có hóa đơn nào
        </div>`;
        return;
    }

    container.innerHTML = filtered.map(inv => {
        // SỬA: So sánh với 'UNPAID'
        const badgeClass = inv.status === 'UNPAID' ? 'unpaid' : 'paid';
        const badgeText = inv.status === 'UNPAID' ? 'Chờ thanh toán' : 'Đã thanh toán';
        // SỬA: So sánh với 'PAID'
        const methodText = inv.status === 'PAID' && inv.method
            ? `<span style="font-size:0.8rem; color:#00b894; margin-left:10px;">(${inv.method})</span>`
            : '';
        const tableName = inv.table ? inv.table : 'Mang về';

        // Format tiền tệ
        const totalFormatted = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(inv.total);

        return `
            <div class="invoice-card" onclick="openInvoiceDetail('${inv.id}')" style="cursor:pointer">
                <div class="inv-left">
                    <div class="inv-icon"><i class="fa-solid fa-receipt"></i></div>
                    <div class="inv-details">
                        <h4>${inv.id} <span class="inv-badge ${badgeClass}">${badgeText}</span> ${methodText}</h4>
                        <p>
                            <span><i class="fa-solid fa-table"></i> Bàn ${tableName}</span>
                            <span><i class="fa-regular fa-clock"></i> ${inv.time}</span>
                            <span><i class="fa-solid fa-layer-group"></i> ${inv.items.length} món</span>
                        </p>
                    </div>
                </div>
                <div class="inv-right">
                    <span class="inv-total">${totalFormatted}</span>
                    <button class="btn-view">Xem chi tiết</button>
                </div>
            </div>
        `;
    }).join('');
}

/* --- 2. STATS --- */
function updateStats() {
    // SỬA: Filter theo 'PAID'
    const paidInvoices = invoicesData.filter(i => i.status === 'PAID');
    const totalRevenue = paidInvoices.reduce((sum, item) => sum + item.total, 0);
    const avg = paidInvoices.length ? totalRevenue / paidInvoices.length : 0;

    document.getElementById('stat-count').innerText = invoicesData.length;
    document.getElementById('stat-revenue').innerText = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(totalRevenue);
    document.getElementById('stat-avg').innerText = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(avg);
}

/* --- 3. MODAL LOGIC --- */
function openInvoiceDetail(id) {
    const inv = invoicesData.find(i => i.id === id);
    if(!inv) return;

    document.getElementById('m-inv-id').innerText = inv.id;
    document.getElementById('m-table').innerText = `#${inv.table || 'Mang về'}`;
    document.getElementById('m-time').innerText = inv.time;
    document.getElementById('m-total').innerText = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(inv.total);

    const listHtml = inv.items.map(item => `
        <li>
            <span class="item-name"><span class="item-qty" style="font-weight:bold">${item.qty}x</span> ${item.name}</span>
            <span class="item-price">${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.price * item.qty)}</span>
        </li>
    `).join('');
    document.getElementById('m-items-list').innerHTML = listHtml;

    const actionContainer = document.getElementById('m-actions');
    const paymentInfoBox = document.getElementById('m-payment-info');

    // SỬA: Check 'UNPAID'
    if (inv.status === 'UNPAID') {
        paymentInfoBox.style.display = 'none';
        actionContainer.innerHTML = `
            <button class="btn-print" onclick="printInvoice('${inv.id}')">
                <i class="fa-solid fa-print"></i> In Bill
            </button>
            <button class="btn-pay" onclick="goToPayment('${inv.id}')">
                <i class="fa-solid fa-credit-card"></i> Thanh Toán
            </button>
        `;
    } else {
        paymentInfoBox.style.display = 'block';
        document.getElementById('m-method').innerText = inv.method;
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

window.onclick = function(e) {
    if (e.target == document.getElementById('invoice-modal')) {
        closeModal();
    }
}

/* --- 4. ACTION --- */
function printInvoice(id) {
    alert(`Đang gửi lệnh in hóa đơn ${id}...`);
}

function goToPayment(id) {
    const inv = invoicesData.find(i => i.id === id);
    if (inv) {
        localStorage.setItem('pendingInvoice', JSON.stringify(inv));
        window.location.href = '/cashier/payment';
    }
}