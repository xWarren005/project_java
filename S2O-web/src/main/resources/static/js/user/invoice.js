/* ===============================
   INVOICE JS - TẠM TÍNH TỪ API
   =============================== */

async function loadInvoice() {
    const content = document.getElementById("invoice-content");
    const tableDisplay = document.getElementById("invoice-table");

    // Hiển thị số bàn từ Header (nếu có)
    const currentTableName = document.getElementById("table-number-display")?.textContent || "";
    if(tableDisplay) tableDisplay.textContent = currentTableName;

    if (!content) return;

    // Loading State
    content.innerHTML = '<div class="loading-state" style="text-align:center; padding:20px;">⏳ Đang tính tiền...</div>';

    try {
        // 1. Gọi API lấy các đơn chưa thanh toán
        const response = await fetch('/api/user/invoice/current');

        if (response.ok) {
            const orders = await response.json();
            renderInvoice(orders);
        } else if (response.status === 401) {
            content.innerHTML = '<div class="invoice-empty">Vui lòng đăng nhập</div>';
        } else {
            content.innerHTML = '<div class="invoice-empty">Lỗi tải hóa đơn</div>';
        }
    } catch (error) {
        console.error("Lỗi:", error);
        content.innerHTML = '<div class="invoice-empty">Lỗi kết nối server</div>';
    }
}

/* ===============================
   INVOICE - FROM ORDERS
   =============================== */
function renderInvoice(orders) {
    const content = document.getElementById("invoice-content")

    if (!orders || orders.length === 0) {
        content.innerHTML = `
            <div class="invoice-empty" style="text-align:center; padding:40px; color:#888;">
                <p>Bàn chưa có đơn hàng nào.</p>
                <button class="btn btn-primary" onclick="switchTab('menu')">Gọi món ngay</button>
            </div>
        `;
        return;
    }
    // --- LOGIC GỘP MÓN (AGGREGATION) ---
    const itemsMap = {};
    let total = 0
    orders.forEach(order => {
        order.items.forEach(item => {
            // Dùng tên món làm Key để gộp (hoặc dùng ProductId nếu API trả về)
            const key = item.productName;
            if (!itemsMap[key]) {
                itemsMap[key] = {
                    name: item.productName,
                    price: item.unitPrice,
                    quantity: 0,
                };
            }
            itemsMap[key].quantity += item.quantity;
            total += (item.unitPrice * item.quantity);
        });
    });
    content.innerHTML = `
        <div class="invoice-card">
            <div class="invoice-items">
                ${Object.values(itemsMap).map(item => `
                    <div class="invoice-item-row">
                        <span><b>${item.quantity}x</b> ${item.name}</span>
                        <span>${formatInvoicePrice(item.price * item.quantity)}</span>
                    </div>
                `).join("")}
            </div>

            <div class="invoice-total">
                <span>Tổng cộng</span>
                <span class="invoice-total-price">
                    ${formatInvoicePrice(total)}
                </span>
            </div>
        <div style="margin-top: 20px;">
                <button onclick="switchTab('payment')" 
                        style="width:100%; padding:12px; background:var(--color-secondary); color:white; border:none; border-radius:var(--radius); font-weight:bold; cursor:pointer;">
                    Thanh toán ngay
                </button>
            </div>
        </div>
    `;
}
function formatInvoicePrice(price) {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
}
