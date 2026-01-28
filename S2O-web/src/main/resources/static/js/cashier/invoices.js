/* ================= GLOBAL STATE ================= */
let invoicesData = [];
let currentFilter = 'UNPAID';

/* ================= INIT ================= */
document.addEventListener("DOMContentLoaded", () => {
    if (typeof renderTopHeader === 'function') renderTopHeader();
    if (typeof renderCashierMenu === 'function') renderCashierMenu('invoices');

    fetchInvoices();
    setInterval(fetchInvoices, 30000);
});

/* ================= FETCH ================= */
async function fetchInvoices() {
    try {
        const res = await fetch('/api/cashier/invoices');
        if (!res.ok) throw new Error('Network error');
        invoicesData = await res.json();
        updateStats();
        filterInvoices(currentFilter);
    } catch (e) {
        console.error(e);
    }
}

/* ================= FILTER ================= */
function filterInvoices(status) {
    currentFilter = status;

    document.querySelectorAll('.filter-btn')
        .forEach(b => b.classList.remove('active'));

    const btns = document.querySelectorAll('.filter-btn');
    if (status === 'UNPAID') btns[0].classList.add('active');
    else btns[1].classList.add('active');

    if (status === 'UNPAID') {
        renderUnpaidAccordion();
    } else {
        renderPaidList();
    }
}

/* ================= UNPAID → ACCORDION THEO BÀN ================= */
function renderUnpaidAccordion() {
    const container = document.getElementById('invoice-list');
    const unpaid = invoicesData.filter(i => i.status === 'UNPAID');

    if (!unpaid.length) {
        container.innerHTML = emptyHtml();
        return;
    }

    // GROUP BY TABLE
    const tableMap = {};
    unpaid.forEach(inv => {
        const key = inv.tableId || 'TAKEAWAY';
        if (!tableMap[key]) {
            tableMap[key] = {
                tableId: inv.tableId,
                tableName: inv.table || 'Mang về',
                orders: []
            };
        }
        tableMap[key].orders.push(inv);
    });

    container.innerHTML = Object.values(tableMap).map(group => {
        const total = group.orders.reduce((s, o) => s + o.total, 0);

        return `
        <div class="accordion">
            <div class="accordion-header" onclick="toggleAccordion(this)">
                <div>
                    <strong>Bàn ${group.tableName}</strong>
                    <span class="badge unpaid">${group.orders.length} hóa đơn</span>
                </div>
                <div>
                    <strong>${formatCurrency(total)}</strong>
                    <i class="fa-solid fa-chevron-down"></i>
                </div>
            </div>

            <div class="accordion-body">
                ${group.orders.map(inv => renderInvoiceRow(inv)).join('')}

                <div class="accordion-footer">
                    <button class="btn-pay"
                        onclick="payByTable(${group.tableId})">
                        <i class="fa-solid fa-credit-card"></i>
                        Thanh toán
                    </button>
                </div>
            </div>
        </div>`;
    }).join('');
}

/* ================= PAID → LIST ================= */
function renderPaidList() {
    const container = document.getElementById('invoice-list');
    const paid = invoicesData.filter(i => i.status === 'PAID');

    if (!paid.length) {
        container.innerHTML = emptyHtml();
        return;
    }

    container.innerHTML = paid.map(inv => renderInvoiceCard(inv)).join('');
}

/* ================= COMPONENTS ================= */
function renderInvoiceRow(inv) {
    return `
    <div class="accordion-row" onclick="openInvoiceDetail('${inv.id}')">
        <span>${inv.id}</span>
        <span>${inv.items.length} món</span>
        <span>${formatCurrency(inv.total)}</span>
    </div>`;
}

function renderInvoiceCard(inv) {
    return `
    <div class="invoice-card" onclick="openInvoiceDetail('${inv.id}')">
        <div class="inv-left">
            <div class="inv-icon"><i class="fa-solid fa-receipt"></i></div>
            <div class="inv-details">
                <h4>${inv.id}
                    <span class="inv-badge paid">Đã thanh toán</span>
                    ${inv.method ? `(${inv.method})` : ''}
                </h4>
                <p>
                    <span><i class="fa-solid fa-table"></i> ${inv.table || 'Mang về'}</span>
                    <span><i class="fa-regular fa-clock"></i> ${inv.time}</span>
                </p>
            </div>
        </div>
        <div class="inv-right">
            <span class="inv-total">${formatCurrency(inv.total)}</span>
        </div>
    </div>`;
}

/* ================= ACTION ================= */
function payByTable(tableId) {
    if (!tableId) {
        alert("Không thể thanh toán cho đơn mang về");
        return;
    }
    window.location.href = `/cashier/payment?tableId=${tableId}`;
}

function toggleAccordion(header) {
    const body = header.nextElementSibling;
    body.classList.toggle('open');
    header.querySelector('i').classList.toggle('rotate');
}

/* ================= MODAL ================= */
function openInvoiceDetail(id) {
    const inv = invoicesData.find(i => i.id === id);
    if (!inv) return;

    document.getElementById('m-inv-id').innerText = inv.id;
    document.getElementById('m-table').innerText = `#${inv.table || 'Mang về'}`;
    document.getElementById('m-time').innerText = inv.time;
    document.getElementById('m-total').innerText = formatCurrency(inv.total);

    document.getElementById('m-items-list').innerHTML = inv.items.map(i => `
        <li>
            <span><b>${i.qty}x</b> ${i.name}</span>
            <span>${formatCurrency(i.price * i.qty)}</span>
        </li>`).join('');

    const actions = document.getElementById('m-actions');
    const paymentInfo = document.getElementById('m-payment-info');

    paymentInfo.style.display = inv.status === 'PAID' ? 'block' : 'none';
    if (inv.status === 'PAID') {
        document.getElementById('m-method').innerText = inv.method || 'N/A';
    }

    actions.innerHTML = `
        <button class="btn-print" onclick="printInvoice('${inv.id}')">
            <i class="fa-solid fa-print"></i> In hóa đơn
        </button>
        ${inv.status === 'UNPAID'
        ? `<button class="btn-pay" onclick="paySingle('${inv.orderId}')">
                   <i class="fa-solid fa-credit-card"></i> Thanh toán
               </button>`
        : ''}
    `;

    document.getElementById('invoice-modal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('invoice-modal').style.display = 'none';
}

window.onclick = e => {
    const modal = document.getElementById('invoice-modal');
    if (e.target === modal) closeModal();
};

/* ================= PRINT ================= */
function printInvoice(id) {
    alert("Chức năng in hóa đơn sẽ được phát triển khi tích hợp máy in.");
}

/* ================= SINGLE PAYMENT ================= */
function paySingle(orderId) {
    if (!orderId) return;
    window.location.href = `/cashier/payment?orderId=${orderId}`;
}

/* ================= STATS ================= */
function updateStats() {
    const paid = invoicesData.filter(i => i.status === 'PAID');
    const total = paid.reduce((s, i) => s + i.total, 0);

    const c = document.getElementById('stat-count');
    if (c) c.innerText = invoicesData.length;

    const r = document.getElementById('stat-revenue');
    if (r) r.innerText = formatCurrency(total);
}

/* ================= HELPERS ================= */
function formatCurrency(v) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(v);
}

function emptyHtml() {
    return `
    <div style="text-align:center;color:#999;padding:3rem">
        <i class="fa-solid fa-clipboard-list" style="font-size:2rem"></i>
        <p>Không có hóa đơn nào</p>
    </div>`;
}
