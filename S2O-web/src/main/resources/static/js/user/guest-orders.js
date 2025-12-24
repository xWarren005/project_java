/* ===============================
   GUEST – ORDERS
   =============================== */

function renderGuestOrders() {
    const el = document.getElementById("orders-list")
    if (!el) return

    const tableNumber = document.getElementById("table-number").textContent
    const orders = Storage.getOrders(tableNumber)

    if (!orders.length) {
        el.innerHTML = "<p>Chưa có đơn hàng nào</p>"
        return
    }

    el.innerHTML = orders.map(o => `
    <div class="order-card">
      <div class="order-header">
        <div>
          <div class="order-id">Đơn #${o.id}</div>
          <div class="order-date">
            ${new Date(o.date).toLocaleString("vi-VN")}
          </div>
        </div>
        <span class="badge badge-success">Đã gọi</span>
      </div>

      <div class="order-items">
        ${o.items.map(i => `
          <div class="order-item-row">
            <span>${i.name} x${i.quantity}</span>
            <span>${formatPrice(i.price * i.quantity)}</span>
          </div>
        `).join("")}
      </div>

      <div class="order-total">
        <span>Tổng</span>
        <span class="order-total-price">
          ${formatPrice(o.total)}
        </span>
      </div>
    </div>
  `).join("")
}

document
    .querySelector('[data-tab="orders"]')
    ?.addEventListener("click", renderGuestOrders)
