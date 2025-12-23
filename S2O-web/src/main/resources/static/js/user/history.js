// User history page
let currentUser = null
function goBack() {
  window.history.back()
}
function goToHistory() { window.location.href = "history.html" }
function goToProfile() { window.location.href = "profile.html" }
// LocalStorage management
const Storage = {
  getCurrentUser() {
    return JSON.parse(localStorage.getItem("currentUser") || "null")
  },
  setCurrentUser(user) {
    localStorage.setItem("currentUser", JSON.stringify(user))
  },
  // Orders
  getOrders(tableNumber) {
    return JSON.parse(localStorage.getItem(`orders_${tableNumber}`) || "[]")
  },
  saveOrder(tableNumber, order) {
    const orders = this.getOrders(tableNumber)
    orders.push(order)
    localStorage.setItem(`orders_${tableNumber}`, JSON.stringify(orders))
  },
  // User History
  getUserHistory(userId) {
    return JSON.parse(
        localStorage.getItem(`history_${userId}`)
    ) || {
      invoices: [],
      totalSpent: 0,
      visitCount: 0,
    }
  }
  ,

  saveUserHistory(userId, history) {
    localStorage.setItem(`history_${userId}`, JSON.stringify(history))
  },

  addToUserHistory(userId, invoice) {
    const history = this.getUserHistory(userId)
    history.invoices.push(invoice)
    history.totalSpent += invoice.total
    history.visitCount += 1
    this.saveUserHistory(userId, history)
  },
}

function formatPrice(price) {
  return price.toLocaleString("vi-VN", { style: "currency", currency: "VND" })
}

function formatDate(date) {
  const options = { year: "numeric", month: "long", day: "numeric" }
  return new Date(date).toLocaleDateString("vi-VN", options)
}

document.addEventListener("DOMContentLoaded", () => {
  currentUser = Storage.getCurrentUser()
  if (!currentUser) {
    window.location.href = "../login.html"
    return
  }

  loadHistory()
})

function loadHistory() {
  const history = Storage.getUserHistory(currentUser.id)
  // Update stats
  document.getElementById("total-spent").textContent = formatPrice(history.totalSpent)
  document.getElementById("total-orders").textContent = history.invoices.length
  document.getElementById("total-visits").textContent = history.visitCount

  // Load history list
  const historyListContainer = document.getElementById("history-list")

  if (history.invoices.length === 0) {
    historyListContainer.innerHTML = '<div class="card"><div class="empty-state">Chưa có lịch sử nào</div></div>'
    return
  }

  historyListContainer.innerHTML = history.invoices
    .reverse()
    .map(
      (invoice) => `
        <div class="history-item">
            <div class="history-header">
                <div class="history-info">
                    <h4>${invoice.id}</h4>
                    <p>${formatDate(invoice.date)} - Bàn ${invoice.tableNumber}</p>
                </div>
                <span class="badge badge-${invoice.status}">${getStatusText(invoice.status)}</span>
            </div>
            <div class="history-items">
                ${invoice.items
                  .map(
                    (item) => `
                    <div class="history-item-row">
                        <span>${item.quantity}x ${item.name}</span>
                        <span>${formatPrice(item.price * item.quantity)}</span>
                    </div>
                `,
                  )
                  .join("")}
            </div>
            <div class="history-total">
                <span>Tổng cộng</span>
                <span class="history-total-price">${formatPrice(invoice.total)}</span>
            </div>
        </div>
    `,
    )
    .join("")
}

function getStatusText(status) {
  const statusMap = {
    pending: "Chờ xử lý",
    preparing: "Đang chuẩn bị",
    ready: "Sẵn sàng",
    completed: "đã thanh toán",
  }
  return statusMap[status] || status
}
