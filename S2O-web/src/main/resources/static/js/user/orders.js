/* ========= ORDERS (History Tab) ========= */

async function loadOrderHistory(showLoading = true) {
    const container = document.getElementById("orders-list");
    if (showLoading) {
        container.innerHTML = '<div class="loading-state" style="text-align:center; padding:20px;">⏳ Đang cập nhật...</div>';
    }

    try {
        // GỌI API MỚI (TỪ OrderHistoryController)
        const response = await fetch('/api/user/orders/orders');

        if (response.ok) {
            const orders = await response.json();
            renderOrders(orders);
        } else if (response.status === 401) {
            container.innerHTML = '<div class="empty-state">Vui lòng <a href="/user/login">đăng nhập</a> để xem lịch sử.</div>';
        } else {
            if (showLoading) {
                container.innerHTML = '<div class="empty-state">Không thể tải dữ liệu.</div>';
            }
        }
    } catch (error) {
        console.error("Lỗi:", error);
        if (showLoading) {
            container.innerHTML = '<div class="empty-state">Lỗi kết nối server.</div>';
        }
    }
}
function renderOrders(orders) {
    const el = document.getElementById("orders-list")
    if (!el) return

    if (!orders || orders.length === 0) {
        el.innerHTML = `<div class="empty-state" style="text-align:center; padding:30px;">
                            <p>Bạn chưa có đơn hàng nào.</p>
                            <button class="btn btn-primary" onclick="switchTab('menu')">Gọi món ngay</button>
                        </div>`;
        return
    }

    el.innerHTML = orders.map(o => `
        <div class="order-card">
            <div class="order-header">
                <div>
                    <div class="order-id">${o.id}</div>
                    <div class="order-date">${formatDateOrders(o.createdAt)}</div>
                </div>
                <span class="badge ${getStatusBadgeClass(o.status)}">
                    ${o.statusVietnamese}
                </span>
            </div>

            <div class="order-items">
                ${o.items.map(i => `
                    <div class="order-item-row">
                        <span>$<b>x${i.quantity}</b> ${i.productName}</span>
                        <span>${formatPriceOrders(i.unitPrice * i.quantity)}</span>
                    </div>
                `).join("")}
            </div>

            <div class="order-total">
                <span>Tổng cộng</span>
                <span class="order-total-price">${formatPriceOrders(o.totalAmount)}</span>
            </div>
        </div>
    `).join("")
}
document.addEventListener("DOMContentLoaded", () => {
    document
        .querySelector('[data-tab="orders"]')
        ?.addEventListener("click", () => loadOrderHistory(true));
});
let orderInterval = null;

function startOrderPolling() {
    if (orderInterval) clearInterval(orderInterval);
    orderInterval = setInterval(() => {
        // Chỉ gọi lại API nếu đang ở tab Orders
        const ordersTab = document.querySelector('.tab[data-tab="orders"]');
        if (ordersTab && ordersTab.classList.contains('active')) {
            loadOrderHistory(false);
        }
    }, 3000); // Cập nhật mỗi 3 giây
}
// Utils
function formatPriceOrders(price) {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
}

function formatDateOrders(dateString) {
    if(!dateString) return "";
    return new Date(dateString).toLocaleString("vi-VN", {
        hour: "2-digit", minute: "2-digit", day: "2-digit", month: "2-digit"
    });
}

function getStatusBadgeClass(status) {
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