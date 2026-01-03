// User history page
let currentUser = null
function goBack() {
  window.history.back()
}
function goToHistory() { window.location.href = "/user/history" }
function goToProfile() { window.location.href = "/user/profile" }

function formatPrice(price) {
  return price.toLocaleString("vi-VN", { style: "currency", currency: "VND" })
}

function formatDate(date) {
  const options = { year: "numeric", month: "long", day: "numeric" }
  return new Date(date).toLocaleDateString("vi-VN", options)
}

document.addEventListener("DOMContentLoaded", () => {
  fetchHistoryData();
});
async function fetchHistoryData() {
  const listContainer = document.getElementById("history-list");

  try {
    const response = await fetch('/api/user/orders/history');

    if (response.status === 401) {
      alert("Phiên đăng nhập hết hạn.");
      window.location.href = "/user/login";
      return;
    }

    if (response.ok) {
      const orders = await response.json();

      // 1. Tính toán thống kê
      calculateStats(orders);

      // 2. Vẽ danh sách hóa đơn
      renderHistoryList(orders);
    } else {
      listContainer.innerHTML = '<div class="empty-state">Không thể tải dữ liệu</div>';
    }
  } catch (error) {
    console.error("Lỗi:", error);
    listContainer.innerHTML = '<div class="empty-state">Lỗi kết nối server</div>';
  }
}
function calculateStats(orders) {
  // Chỉ tính các đơn đã hoàn thành hoặc đã thanh toán (Tùy logic nhà hàng)
  // Ở đây mình tính tất cả đơn KHÔNG bị hủy
  const validOrders = orders.filter(o => o.status !== 'CANCELLED');

  const totalSpent = validOrders.reduce((sum, order) => sum + (order.totalAmount || 0), 0);
  const totalOrders = validOrders.length;
  const totalVisits = validOrders.length; // Tạm tính 1 đơn = 1 lần ghé thăm

  // Update UI
  animateValue("total-spent", totalSpent, formatPrice);
  document.getElementById("total-orders").textContent = totalOrders;
  document.getElementById("total-visits").textContent = totalVisits;
}

function renderHistoryList(orders) {
  const listContainer = document.getElementById("history-list");

  if (!orders || orders.length === 0) {
    listContainer.innerHTML = `
            <div class="empty-state" style="text-align: center; padding: 30px;">
                <img src="/images/empty-history.png" onerror="this.style.display='none'" style="width: 60px; opacity: 0.5;">
                <p>Bạn chưa có lịch sử giao dịch nào.</p>
            </div>`;
    return;
  }
  ListContainer.innerHTML = orders.map(order => `
        <div class="history-item">
            <div class="history-header">
                <div class="history-info">
                    <h4>${order.id}</h4>
                    <p>${formatDate(order.createdAt)}</p>
                </div>
                <span class="badge ${getStatusClass(order.status)}">
                    ${order.statusVietnamese || order.status}
                </span>
            </div>
            <div class="history-items">
                ${order.items.map(item => `
                    <div class="history-item-row">
                        <span>${item.quantity}x ${item.productName}</span>
                        <span>${formatPrice(item.unitPrice * item.quantity)}</span>
                    </div>
                `,
                  )
                  .join("")}
            </div>
            <div class="history-total">
                <span>Tổng cộng</span>
                <span class="history-total-price">${formatPrice(order.totalAmount)}</span>
            </div>
        </div>
    `).join("")
}

function getStatusClass(status) {
  switch(status) {
  case 'PENDING': return 'badge-warning';   // class CSS trong history.css
  case 'CONFIRMED': return 'badge-info';
  case 'COMPLETED': return 'badge-success';
  case 'PAID': return 'badge-dark';
  case 'CANCELLED': return 'badge-danger';
  default: return 'badge-secondary';
  }
  }
// Hiệu ứng chạy số (Optional - cho đẹp)
function animateValue(id, value, formatFunc) {
  const obj = document.getElementById(id);
  if(!obj) return;
  obj.textContent = formatFunc(value);
}