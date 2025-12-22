/* ========= 1. COMMON ========= */
function formatPrice(price) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price)
}
function goToProfile() { window.location.href = "profile.html" }
/* ========= 2. TAB ========= */
function switchTab(tab) {
  document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"))
  document.querySelector(`[data-tab="${tab}"]`)?.classList.add("active")

  document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"))
  document.getElementById(`tab-${tab}`)?.classList.add("active")

}

/* ========= 3. STORAGE ========= */
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
  clearOrders(table) {
    localStorage.removeItem(`orders_${table}`)
  }
}

/* ========= 4. MOCK DATA ========= */
const MockData = {
  categories: [
    { id: "all", name: "Tất cả" },
    { id: "main", name: "Món chính" },
    { id: "drink", name: "Đồ uống" },
  ],
  menuItems: [
    {
      id: "pho",
      name: "Phở Bò",
      description: "Phở bò truyền thống",
      price: 65000,
      category: "main",
      image: "../../../../../public/pho-bo-vietnamese-beef-noodle-soup.jpg",
    },
    {
      id: "comtam",
      name: "Cơm Tấm",
      description: "Cơm tấm sườn bì",
      price: 50000,
      category: "main",
      image: "../../../../../public/com-tam-broken-rice.jpg",
    },
    {
      id: "trada",
      name: "Trà đá",
      description: "Trà đá mát lạnh",
      price: 10000,
      category: "drink",
      image: "../../../../../public/iced-tea.jpg",
    },
  ],
  getCategories() { return this.categories },
  getMenuItems(category, search = "") {
    let items = this.menuItems
    if (category !== "all") items = items.filter(i => i.category === category)
    if (search) items = items.filter(i => i.name.toLowerCase().includes(search.toLowerCase()))
    return items
  },
  getMenuItem(id) {
    return this.menuItems.find(i => i.id === id)
  }
}

/* ========= 5. MENU ========= */
const tableNumber = "A5"
let selectedCategory = "all"
let cart = []

document.addEventListener("DOMContentLoaded", () => {
  cart = Storage.getCart(tableNumber)
  document.getElementById("table-number").textContent = tableNumber
  document.getElementById("invoice-table").textContent = tableNumber
  loadCategories()
  loadMenuItems()
  updateCartBadge()
})

function loadCategories() {
  document.getElementById("categories").innerHTML =
      MockData.getCategories().map(c => `
        <button class="category-btn ${c.id === selectedCategory ? "active" : ""}"
            onclick="selectCategory('${c.id}')">${c.name}</button>
    `).join("")
}

function selectCategory(id) {
  selectedCategory = id
  loadCategories()
  loadMenuItems()
}

function searchMenu() { loadMenuItems() }

function loadMenuItems() {
  const search = document.getElementById("search-input").value
  const items = MockData.getMenuItems(selectedCategory, search)
  const el = document.getElementById("menu-items")

  if (!items.length) {
    el.innerHTML = `<div class="empty-state">Không có món</div>`
    return
  }

  el.innerHTML = items.map(i => `
        <div class="menu-item-card">
            <img class="menu-item-image" src="${i.image}"
                onerror="this.src='../public/placeholder.svg'">
            <div class="menu-item-content">
                <h3>${i.name}</h3>
                <p>${i.description}</p>
                <div class="menu-item-footer">
                    <span>${formatPrice(i.price)}</span>
                    <button class="btn-add" onclick="addToCart('${i.id}')">Thêm</button>
                </div>
            </div>
        </div>
    `).join("")
}

/* ========= 6. CART ========= */
function addToCart(id) {
  const item = MockData.getMenuItem(id)
  const exist = cart.find(i => i.id === id)
  exist ? exist.quantity++ : cart.push({ ...item, quantity: 1 })
  saveCart()
}

function increaseQty(id) {
  const i = cart.find(i => i.id === id)
  if (i) i.quantity++
  saveCart()
}

function decreaseQty(id) {
  const i = cart.find(i => i.id === id)
  if (!i) return
  i.quantity--
  if (i.quantity <= 0) cart = cart.filter(x => x.id !== id)
  saveCart()
}

function removeItem(id) {
  cart = cart.filter(i => i.id !== id)
  saveCart()
}

function saveCart() {
  Storage.saveCart(tableNumber, cart)
  updateCartBadge()
  renderCart()
}

function renderCart() {
  const el = document.getElementById("cart-items")
  if (!cart.length) {
    el.innerHTML = "<p>Giỏ hàng trống</p>"
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
function updateCartBadge() {
  const total = cart.reduce((s, i) => s + i.quantity, 0)
  const badge = document.getElementById("cart-badge")
  badge.textContent = total
  badge.style.display = total ? "flex" : "none"
}

function toggleCart() {
  document.getElementById("cart-overlay").classList.toggle("active")
  document.getElementById("cart-sidebar").classList.toggle("active")
}

/* ========= 7. ORDER ========= */
function placeOrder() {
  if (!cart.length) {
    alert("Giỏ hàng trống!")
    return
  }
  const total = cart.reduce((s, i) => s + i.price * i.quantity, 0)
  Storage.saveOrder(tableNumber, {
    id: "ORD-" + Date.now(),
    tableNumber,
    items: cart,
    total,
    date: new Date().toISOString(),
    status: "ordered",
  })

  cart = []
  saveCart()
  toggleCart()
  switchTab("orders")
  alert("Đã gửi đơn cho nhà bếp!")
}
