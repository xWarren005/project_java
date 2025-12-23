/* ===============================
   INVOICE - FROM ORDERS
   =============================== */
function renderInvoice() {
    const content = document.getElementById("invoice-content")
    if (!content) return

    const tableNumber = document.getElementById("table-number").textContent
    document.getElementById("invoice-table").textContent = tableNumber

    const orders = Storage.getOrders(tableNumber)

    if (!orders.length) {
        content.innerHTML = `
            <div class="invoice-empty">
                Bàn chưa có đơn hàng
            </div>
        `
        return
    }

    // gộp món từ nhiều order
    const itemsMap = {}
    let total = 0
    orders.forEach(order => {
        order.items.forEach(item => {
            if (!itemsMap[item.id]) {
                itemsMap[item.id] = { ...item }
            } else {
                itemsMap[item.id].quantity += item.quantity
            }
            total += item.price * item.quantity
        })
    })
    content.innerHTML = `
        <div class="invoice-card">
            <div class="invoice-items">
                ${Object.values(itemsMap).map(i => `
                    <div class="invoice-item-row">
                        <span>${i.quantity} x ${i.name}</span>
                        <span>${formatPrice(i.price * i.quantity)}</span>
                    </div>
                `).join("")}
            </div>

            <div class="invoice-total">
                <span>Tổng cộng</span>
                <span class="invoice-total-price">
                    ${formatPrice(total)}
                </span>
            </div>
        </div>
    `
}
