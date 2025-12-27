
/* ========= 1. COMMON / UTIL ========= */
function formatPrice(price) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(price)
}

function formatDate(date) {
    return new Date(date).toLocaleString("vi-VN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    })
}

function generateId() {
    return "id_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9)
}

/* ========= 2. NAVIGATION ========= */
function goToHistory() { window.location.href = "history.html" }
function goToProfile() { window.location.href = "/user/profile" }
function switchTab(tab) {
    // 1. bỏ active khỏi tất cả nút
    document.querySelectorAll(".tab").forEach(t =>
        t.classList.remove("active")
    )

    // 2. active nút đang click
    document.querySelector(`.tab[data-tab="${tab}"]`)
        .classList.add("active")
    // 3. ẩn toàn bộ tab-content
    document.querySelectorAll(".tab-content").forEach(c =>
        c.classList.remove("active")
    )
    // 4. hiện tab được chọn
    document.getElementById(`tab-${tab}`)
        .classList.add("active")
}
/* ========= 3. STORAGE (LocalStorage) ========= */
const Storage = {
    getCart(table) {
        return JSON.parse(localStorage.getItem(`cart_${table}`) || "[]")
    },
    saveCart(table, cart) {
        localStorage.setItem(`cart_${table}`, JSON.stringify(cart))
    },
    getOrders(table) {
        return JSON.parse(localStorage.getItem(`orders_${table}`) || "[]")
    },
    saveOrder(table, order) {
        const orders = this.getOrders(table)
        orders.push(order)
        localStorage.setItem(`orders_${table}`, JSON.stringify(orders))
    },
    getCurrentUser() {
        return JSON.parse(localStorage.getItem("currentUser") || "null")
    },
    addToUserHistory(userId, invoice) {
        const key = `history_${userId}`
        const history = JSON.parse(localStorage.getItem(key) || '{"invoices":[],"totalSpent":0,"visitCount":0}')
        history.invoices.push(invoice)
        history.totalSpent += invoice.total
        history.visitCount += 1
        localStorage.setItem(key, JSON.stringify(history))
    },
}

/* ========= 4. MOCK DATA ========= */
const MockData = {
    categories: [
        { id: "all", name: "Tất cả" },
        { id: "appetizers", name: "Khai vị" },
        { id: "main", name: "Món chính" },
        { id: "drink", name: "Đồ uống" },
    ],

    menuItems: [
        {
            id: "pho",
            name: "Phở Bò",
            description: "Phở bò truyền thống",
            price: 650,
            category: "main",
            image: "../../../../../../public/pho-bo-vietnamese-beef-noodle-soup.jpg",
        },
        {
            id: "comtam",
            name: "Cơm Tấm",
            description: "Cơm tấm sườn bì",
            price: 50000,
            category: "main",
            image: "../../../../../../public/com-tam-broken-rice.jpg",
        },
        {
            id: "trada",
            name: "Trà đá",
            description: "Trà đá mát lạnh",
            price: 10000,
            category: "drink",
            image: "../../../../../../public/iced-tea.jpg",
        },
    ],

    getCategories() { return this.categories },

    getMenuItems(category = "all", search = "") {
        let items = this.menuItems
        if (category !== "all") items = items.filter(i => i.category === category)
        if (search) {
            const q = search.toLowerCase()
            items = items.filter(i => i.name.toLowerCase().includes(q))
        }
        return items
    },

    getMenuItem(id) {
        return this.menuItems.find(i => i.id === id)
    },
}

/* ========= 5. MENU LOGIC ========= */
let selectedCategory = "all"
let cart = []
const tableNumber = "A5"
let currentUser = null

/* INIT */
document.addEventListener("DOMContentLoaded", () => {
    currentUser = Storage.getCurrentUser()
    if (!currentUser) return

    cart = Storage.getCart(tableNumber)
    loadCategories()
    loadMenuItems()
    updateCartBadge()

    document.getElementById("table-number").textContent = tableNumber
    document.getElementById("invoice-table").textContent = tableNumber
})

/* ========= CATEGORY ========= */
function loadCategories() {
    const el = document.getElementById("categories")
    el.innerHTML = MockData.getCategories().map(c => `
    <button class="category-btn ${c.id === selectedCategory ? "active" : ""}" 
      onclick="selectCategory('${c.id}')">${c.name}</button>
  `).join("")
}

function selectCategory(id) {
    selectedCategory = id
    loadCategories()
    loadMenuItems()
}

/* ========= MENU ========= */
function searchMenu() { loadMenuItems() }

function loadMenuItems() {
    const search = document.getElementById("search-input").value
    const el = document.getElementById("menu-items")
    const items = MockData.getMenuItems(selectedCategory, search)

    if (!items.length) {
        el.innerHTML = '<div class="empty-state">Không có món</div>'
        return
    }

    el.innerHTML = items.map(i => `
    <div class="menu-item-card">
      <img class="menu-item-image" src="${i.image}" onerror="this.src='../public/placeholder.svg'">
      <div class="menu-item-content">
      <h3 class="menu-item-name">${i.name}</h3>
      <p class="menu-item-desc">${i.description}</p>
      <div class="menu-item-footer">
        <span> &nbsp; ${formatPrice(i.price)}</span>
        <button class="btn-add" onclick="addToCart('${i.id}')">Thêm</button>
      </div>
      </div>
    </div>
  `).join("")
}

function renderCart() {
    const el = document.getElementById("cart-items")

    if (!cart.length) {
        el.innerHTML = "<p>Giỏ hàng trống</p>"
        document.getElementById("cart-count").textContent = "Giỏ hàng trống"
        document.getElementById("cart-total").textContent = "0đ"
        return
    }

    el.innerHTML = cart.map(i => `
        <div class="cart-item">
            <img class="cart-item-image" src="${i.image}">
            <div class="cart-item-info">
                <div class="cart-item-name">${i.name}</div>
                <div class="cart-item-price">${formatPrice(i.price)}</div>

                <div class="cart-item-controls">
                    <button class="btn-quantity" onclick="decreaseQty('${i.id}')">−</button>
                    <span class="quantity-text">${i.quantity}</span>
                    <button class="btn-quantity" onclick="increaseQty('${i.id}')">+</button>

                    <button class="btn-remove" onclick="removeItem('${i.id}')">✕</button>
                </div>
            </div>
        </div>
    `).join("")

    updateCartTotal()
}
function updateCartTotal() {
    const total = cart.reduce((sum, i) => sum + i.price * i.quantity, 0)
    document.getElementById("cart-total").textContent = formatPrice(total)
    document.getElementById("cart-count").textContent = `${cart.length} món`
}

/* ========= CART ========= */
function addToCart(id) {
    const item = MockData.getMenuItem(id)
    const exist = cart.find(i => i.id === id)
    exist ? exist.quantity++ : cart.push({ ...item, quantity: 1 })
    Storage.saveCart(tableNumber, cart)
    updateCartBadge()
    renderCart()
}

function updateCartBadge() {
    const badge = document.getElementById("cart-badge")
    const total = cart.reduce((s, i) => s + i.quantity, 0)
    badge.textContent = total
    badge.style.display = total ? "flex" : "none"
}

function toggleCart() {
    document.getElementById("cart-overlay").classList.toggle("active")
    document.getElementById("cart-sidebar").classList.toggle("active")
}

/* ========= ORDER ========= */
function placeOrder() {
    if (!cart.length) return

    const total = cart.reduce((s, i) => s + i.price * i.quantity, 0)
    const order = {
        id: "ORD-" + Date.now(),
        tableNumber,
        items: cart,
        total,
        status: "pending",
        date: new Date().toISOString(),
    }

    Storage.saveOrder(tableNumber, order)
    cart = []
    Storage.saveCart(tableNumber, cart)
    updateCartBadge()
    toggleCart()
    alert("Đặt món thành công!")
}
function increaseQty(id) {
    const item = cart.find(i => i.id === id)
    if (!item) return
    item.quantity++
    saveCartAndUpdate()
}

function decreaseQty(id) {
    const item = cart.find(i => i.id === id)
    if (!item) return

    item.quantity--
    if (item.quantity <= 0) {
        cart = cart.filter(i => i.id !== id)
    }
    saveCartAndUpdate()
}

function removeItem(id) {
    cart = cart.filter(i => i.id !== id)
    saveCartAndUpdate()
}

function saveCartAndUpdate() {
    Storage.saveCart(tableNumber, cart)
    updateCartBadge()
    renderCart()
}
