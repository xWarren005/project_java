/* ===============================
   GUEST – PAYMENT
   =============================== */

function renderGuestPayment() {
    const el = document.getElementById("payment-content")
    if (!el) return

    const tableNumber = document.getElementById("table-number").textContent
    const orders = Storage.getOrders(tableNumber)

    if (!orders.length) {
        el.innerHTML = `
      <div class="payment-empty">
        Không có đơn hàng để thanh toán
      </div>
    `
        return
    }

    const total = orders.reduce((sum, o) => sum + o.total, 0)

    el.innerHTML = `
    <div class="payment-card">
      <div class="payment-total">
        <span>Tổng thanh toán</span>
        <strong>${formatPrice(total)}</strong>
      </div>

      <div class="payment-methods">
        <label><input type="radio" checked> Tiền mặt</label>
        <label><input type="radio"> Chuyển khoản</label>
      </div>

      <button class="btn btn-primary btn-full"
        onclick="confirmGuestPayment()">
        yêu cầu thanh toán
      </button>
    </div>
  `
}

function confirmGuestPayment() {
    const tableNumber = document.getElementById("table-number").textContent

    Storage.clearOrders(tableNumber)
    localStorage.removeItem(`cart_${tableNumber}`)

    alert("Thanh toán thành công!")

    renderGuestOrders()
    renderGuestInvoice()
    renderGuestPayment()
}

document
    .querySelector('[data-tab="payment"]')
    ?.addEventListener("click", renderGuestPayment)
