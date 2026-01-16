/* ============================================================
   GUEST INVOICE JS - KẾT NỐI API HÓA ĐƠN
   ============================================================ */

async function renderGuestInvoice() {
    const content = document.getElementById("invoice-content");
    if (!content) return;

    // 1. Cập nhật tên bàn (Lấy từ biến toàn cục trong guest-menu.js)
    const tableDisplay = document.getElementById("invoice-table");
    if (tableDisplay && typeof TABLE_NAME !== 'undefined') {
        tableDisplay.textContent = TABLE_NAME;
    }

    // 2. Hiển thị Loading
    content.innerHTML = `
        <div style="text-align:center; padding: 40px; color: #666;">
            <div class="spinner-border" role="status"></div>
            <p>⏳ Đang tải hóa đơn...</p>
        </div>
    `;

    // 3. Gọi API lấy dữ liệu
    try {
        if (!TABLE_ID) {
            content.innerHTML = '<div class="invoice-empty">Mất thông tin bàn. Vui lòng quét lại QR.</div>';
            return;
        }

        const res = await fetch(`/api/guest/invoice/${TABLE_ID}`);

        if (res.ok) {
            const orders = await res.json();
            renderInvoiceHTML(orders);
        } else {
            content.innerHTML = '<div class="invoice-empty">Không thể tải dữ liệu hóa đơn</div>';
        }
    } catch (e) {
        console.error(e);
        content.innerHTML = '<div class="invoice-empty">Lỗi kết nối server</div>';
    }
}

function renderInvoiceHTML(orders) {
    const content = document.getElementById("invoice-content");

    // Trường hợp chưa gọi món nào
    if (!orders || orders.length === 0) {
        content.innerHTML = `
            <div class="invoice-empty">
                <p>Bàn chưa có đơn hàng nào</p>
                <button class="btn btn-primary" onclick="switchTab('menu')" 
                    style="margin-top:10px; background:var(--color-primary); color:white; border:none; padding:10px 20px; border-radius:8px; cursor:pointer;">
                    Gọi món ngay
                </button>
            </div>
        `;
        return;
    }

    // --- LOGIC GỘP MÓN (AGGREGATION) ---
    // API trả về danh sách các Đơn hàng (Order), mỗi đơn có nhiều món.
    // Chúng ta cần gộp tất cả lại thành một danh sách món duy nhất để in hóa đơn tổng.
    const itemsMap = {};
    let total = 0;

    orders.forEach(order => {
        // Chỉ tính tiền các món không bị Hủy (mặc dù API đã lọc rồi, check lại cho chắc)
        if (order.status !== 'CANCELLED') {
            order.items.forEach(item => {
                // Dùng tên món làm khóa để gộp (hoặc dùng ProductId nếu DTO có trả về)
                const key = item.productName;

                if (!itemsMap[key]) {
                    itemsMap[key] = {
                        name: item.productName,
                        price: item.unitPrice,
                        quantity: 0
                    };
                }

                itemsMap[key].quantity += item.quantity;
                total += item.unitPrice * item.quantity;
            });
        }
    });

    // --- RENDER GIAO DIỆN ---
    content.innerHTML = `
    <div class="invoice-card">
        <div class="invoice-header-line" style="border-bottom: 1px dashed #ddd; padding-bottom: 10px; margin-bottom: 15px; display: flex; justify-content: space-between; color: #666; font-size: 0.9em;">
             <span>${new Date().toLocaleDateString('vi-VN')}</span>
             <span>${orders.length} lần gọi</span>
        </div>

        <div class="invoice-items">
            ${Object.values(itemsMap).map(i => `
            <div class="invoice-item-row">
                <div style="display:flex; gap:10px;">
                    <span style="font-weight:bold; width:30px;">${i.quantity}x</span>
                    <span>${i.name}</span>
                </div>
                <span>${formatPrice(i.price * i.quantity)}</span>
            </div>
            `).join("")}
        </div>

        <div class="invoice-divider" style="border-top: 2px solid #eee; margin: 15px 0;"></div>

        <div class="invoice-total">
            <span>Tổng tạm tính</span>
            <span class="invoice-total-price">
            ${formatPrice(total)}
            </span>
        </div>

        <div style="margin-top: 20px;">
            <button class="btn btn-primary" onclick="switchTab('payment')" 
                style="width:100%; padding: 12px; background: var(--color-secondary, #e67e22); color: white; border: none; border-radius: 8px; font-weight: bold; font-size: 16px; cursor: pointer;">
                Thanh toán ngay
            </button>
        </div>
    </div>
  `;
}

// Event Listener này không bắt buộc nếu guest-menu.js đã gọi hàm này trong switchTab
// Nhưng giữ lại để đảm bảo tính độc lập
document
    .querySelector('[data-tab="invoice"]')
    ?.addEventListener("click", renderGuestInvoice);