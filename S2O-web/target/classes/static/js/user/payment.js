/* ===============================
   PAYMENT LOGIC
   =============================== */
function renderPayment() {
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
                <label>
                    <input type="radio" name="payment" checked>
                    Tiền mặt
                </label>
                <label>
                    <input type="radio" name="payment">
                    Chuyển khoản
                </label>
            </div>

            <button class="btn btn-primary btn-full"
                onclick="confirmPayment(${total})">
                Xác nhận thanh toán
            </button>
        </div>
    `
}

function confirmPayment(total) {
    const tableNumber = document.getElementById("table-number").textContent
    const user = Storage.getCurrentUser()
    if (!user) return
    // 1️⃣ tạo invoice
    const invoice = Storage.getInvoiceFromOrders(tableNumber)
    if (!invoice) return
    // 2️⃣ lưu vào history
    Storage.addToUserHistory(user.id, invoice)
    // 3️⃣ xóa dữ liệu tạm
    Storage.clearOrders(tableNumber)
    localStorage.removeItem(`cart_${tableNumber}`)
    alert("Thanh toán thành công!")
    // 4️⃣ refresh UI
    renderOrders()
    renderInvoice()
    renderPayment()
}

