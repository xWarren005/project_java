/* ============================================================
   GUEST ORDERS JS - THEO DÕI TRẠNG THÁI MÓN
   ============================================================ */

let guestOrderInterval = null; // Biến lưu interval để auto-refresh

async function renderGuestOrders() {
    const el = document.getElementById("orders-list");
    if (!el) return;

    // 1. Kiểm tra ID Bàn
    if (!TABLE_ID) {
        el.innerHTML = "<div class='empty-state'>Lỗi: Không tìm thấy thông tin bàn</div>";
        return;
    }

    // 2. Hiện Loading lần đầu
    el.innerHTML = `
        <div style="text-align:center; padding:20px;">
            <div class="spinner-border text-primary" role="status"></div>
            <p>Đang cập nhật trạng thái...</p>
        </div>
    `;

    // 3. Gọi hàm tải dữ liệu ngay lập tức
    await fetchAndRenderOrders(el);

    // 4. Bật chế độ Polling (Tự động cập nhật mỗi 5 giây)
    // Để khách thấy trạng thái chuyển từ "Đang nấu" -> "Đã xong"
    if (guestOrderInterval) clearInterval(guestOrderInterval);
    guestOrderInterval = setInterval(() => fetchAndRenderOrders(el), 5000);
}

// Hàm tách riêng để dùng cho Polling
async function fetchAndRenderOrders(container) {
    // Kiểm tra nếu người dùng đã chuyển sang tab khác thì dừng render
    if (!document.querySelector('[data-tab="orders"]').classList.contains("active")) {
        return;
    }

    try {
        // GỌI API: /api/guest/tracking/{tableId}
        const res = await fetch(`/api/guest/tracking/${TABLE_ID}`);

        if (res.ok) {
            const orders = await res.json();
            renderOrdersHTML(container, orders);
        } else {
            container.innerHTML = "<div class='empty-state'>Không thể tải dữ liệu</div>";
        }
    } catch (e) {
        console.error("Lỗi tracking:", e);
        // Không xóa nội dung cũ nếu lỗi mạng để khách vẫn xem được cái cũ
    }
}

function renderOrdersHTML(container, orders) {
    if (!orders || orders.length === 0) {
        showEmptyState(container);
        return;
    }
    const activeOrders = orders.filter(o => o.status !== 'PAID' && o.status !== 'CANCELLED');

    // Nếu sau khi lọc mà không còn đơn nào (tức là đã trả tiền hết rồi) -> Hiện trống
    if (activeOrders.length === 0) {
        showEmptyState(container);
        return;
    }

    container.innerHTML = activeOrders.map(o => `
    <div class="order-card">
      <div class="order-header">
        <div>
          <div class="order-id">Đơn #${o.id}</div>
          <div class="order-date">
            ${new Date(o.createdAt).toLocaleTimeString("vi-VN", {hour: '2-digit', minute:'2-digit'})}
            <span style="font-size:0.85em; color:#888;">
                (${new Date(o.createdAt).toLocaleDateString("vi-VN")})
            </span>
          </div>
        </div>
        
        <span class="badge ${getBadgeClass(o.status)}">
            ${o.statusVietnamese}
        </span>
      </div>

      <div class="order-items">
        ${o.items.map(i => `
          <div class="order-item-row">
            <span><b>${i.quantity}x</b> ${i.productName}</span>
            <span>${formatPrice(i.unitPrice * i.quantity)}</span>
          </div>
        `).join("")}
      </div>

      <div class="order-total">
        <span>Tổng đơn</span>
        <span class="order-total-price">
          ${formatPrice(o.totalAmount)}
        </span>
      </div>
    </div>
  `).join("");
}
// Hàm hiển thị trạng thái trống
function showEmptyState(container) {
    container.innerHTML = `
        <div class="empty-state">
            <p>Bạn chưa gọi món nào</p>
            <button class="btn btn-primary" onclick="switchTab('menu')" 
                style="margin-top:10px; background:var(--color-secondary); border:none; color:white; padding:8px 16px; border-radius:4px;">
                Xem thực đơn
            </button>
        </div>
    `;
}

// Hàm chọn màu cho badge dựa trên trạng thái Server trả về
function getBadgeClass(status) {
    switch (status) {
        case 'PENDING':         return 'badge-warning'; // Vàng: Chờ xác nhận
        case 'COOKING':         return 'badge-info';    // Xanh dương: Đang nấu
        case 'READY':           return 'badge-primary'; // Xanh đậm: Đã xong
        case 'COMPLETED':       return 'badge-success'; // Xanh lá: Đang ăn
        case 'PAYMENT_PENDING': return 'badge-danger';  // Đỏ: Chờ thanh toán
        case 'PAID':            return 'badge-secondary'; // Xám: Đã xong xuôi
        case 'CANCELLED':       return 'badge-dark';    // Đen: Hủy
        default:                return 'badge-secondary';
    }
}

// Gắn sự kiện click vào Tab
document
    .querySelector('[data-tab="orders"]')
    ?.addEventListener("click", renderGuestOrders);