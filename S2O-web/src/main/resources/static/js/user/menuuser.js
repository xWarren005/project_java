
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
// Lấy ID từ HTML
const restaurantId = document.getElementById("restaurant-id")?.value;
const tableId = document.getElementById("table-id")?.value;

function generateId() {
    return "id_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9)
}
/* ========= 2. NAVIGATION ========= */
function goToHistory() { window.location.href = "/user/history" }
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
    // 2. KẾT NỐI VỚI FILE orders.js
    if (tab === 'orders') {
        // Kiểm tra xem hàm loadOrderHistory đã tồn tại chưa (do file orders.js load)
        if (typeof loadOrderHistory === "function") {
            loadOrderHistory(true);
        if (typeof startOrderPolling === "function") {
            startOrderPolling();
        }
        }
    }
    if (tab === 'invoice') {
        if (typeof loadInvoice === "function") {
            loadInvoice();
        }
    }
    if (tab === 'payment') {
        if (typeof renderPayment === "function") {
            renderPayment();
        }
    }
}
/* ========= 3. STORAGE (LocalStorage) ========= */
const Storage = {
    getCart(table) {
        return JSON.parse(localStorage.getItem(`cart_${restaurantId}`) || "[]")
    },
    saveCart(table, cart) {
        localStorage.setItem(`cart_${restaurantId}`, JSON.stringify(cart))
    },
    getOrders(table) {
        return JSON.parse(localStorage.getItem(`orders_${table}`) || "[]")
    },
    saveOrder(table, order) {
        const orders = this.getOrders(table)
        orders.push(order)
        localStorage.setItem(`orders_${table}`, JSON.stringify(orders))
    },
    clearCart() {
        localStorage.removeItem(`cart_res_${restaurantId}`);
    },
    getCurrentUser() {
        return JSON.parse(localStorage.getItem("currentUser") || "null")
    },
}
/* ========= 4. SERVER DATA (Thay thế MockData) ========= */
let ServerData = {
    categories: [],
    menuItems: [],

// Getter giống hệt MockData cũ
    getCategories() { return this.categories },

    getMenuItems(category = "all", search = "") {
        let items = this.menuItems
        if (category !== "all"){ items = items.filter(i => i.category === category)}
        if (search) {
            const q = search.toLowerCase()
            items = items.filter(i => i.name.toLowerCase().includes(q))
        }
        return items
    },

    getMenuItem(id) {
        return this.menuItems.find(i => i.id === id);
    },
}

/* ========= 5. MENU LOGIC ========= */
let selectedCategory = "all"
let cart = []

/* INIT */
document.addEventListener("DOMContentLoaded",async () => {
    cart = Storage.getCart();
    mergeGuestCartToUserCart();
// 2. GỌI API LẤY MENU TỪ DB
    if(restaurantId) {
        await fetchMenuData();
    } else {
        console.error("Lỗi: Không tìm thấy Restaurant ID");
    }

    // 3. Update UI
    updateCartBadge();
    renderCart();
});
/* --- HÀM GỘP GIỎ HÀNG  --- */
function mergeGuestCartToUserCart() {
    const guestTableId = localStorage.getItem("currentTableId") || tableId;
    if (!guestTableId) return;

    const guestCartKey = `guest_cart_${guestTableId}`;
    const guestCartJson = localStorage.getItem(guestCartKey);

    if (guestCartJson) {
        try {
            const guestCart = JSON.parse(guestCartJson);

            // 🔥 SỬA LỖI: Kiểm tra kỹ 'guestCart' phải là mảng và không null
            if (guestCart && Array.isArray(guestCart) && guestCart.length > 0) {

                guestCart.forEach(gItem => {
                    const existItem = cart.find(cItem => cItem.id === gItem.id);
                    const qty = parseInt(gItem.quantity) || 1;

                    if (existItem) {
                        existItem.quantity += qty;
                    } else {
                        // Thêm mới
                        cart.push({
                            ...gItem,
                            quantity: qty,
                            price: parseFloat(gItem.price)
                        });
                    }
                });

                // Lưu lại vào Storage của User
                Storage.saveCart(null, cart);

                // Cập nhật UI
                updateCartBadge();
                renderCart();
            }
        } catch (e) {
            console.error("Lỗi khi gộp giỏ hàng:", e);
        }

        // Xóa giỏ hàng khách cũ để tránh lỗi lặp lại
        localStorage.removeItem(guestCartKey);
    }
}
/* --- HÀM GỌI API (Tách ra cho gọn) --- */
async function fetchMenuData() {
    try {
        const res = await fetch('/api/user/menu-data');
        if(res.ok) {
            const data = await res.json();

            // Đổ dữ liệu từ API vào biến ServerData
            ServerData.categories = data.categories;
            ServerData.menuItems = data.menuItems;

            // Render giao diện
            loadCategories();
            loadMenuItems();
        } else {
            console.error("Lỗi HTTP:", res.status);
            if(res.status === 401) window.location.href = "/user/login";
        }
    } catch(e) {
        console.error("Lỗi tải menu:", e);
        document.getElementById("menu-items").innerHTML = "<div class='empty-state'>Lỗi kết nối server</div>";
    }
}

/* ========= CATEGORY ========= */
function loadCategories() {
    const el = document.getElementById("categories")
    el.innerHTML = ServerData.getCategories().map(c => `
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
    const items = ServerData.getMenuItems(selectedCategory, search)

    if (!items.length) {
        el.innerHTML = '<div class="empty-state">Không có món</div>'
        return
    }

    el.innerHTML = items.map(i =>{
        // Xử lý ảnh null
        const imgUrl = (i.image && i.image.trim() !== "") ? i.image : "/images/default-food.png";
// LOGIC HIỂN THỊ GIÁ
        let priceHtml = '';
        let badgeHtml = '';

        if (i.discount && i.discount > 0) {
            const originalPrice = i.price;
            const discountedPrice = originalPrice * (1 - i.discount / 100);

            // Giá đỏ + Giá cũ gạch ngang
            priceHtml = `
                <div style="display:flex; flex-direction:column; align-items:flex-start;">
                    <span style="color:#ef4444; font-weight:700; font-size:18px;">
                        ${formatPrice(discountedPrice)}
                    </span>
                    <span style="text-decoration:line-through; color:#9ca3af; font-size:13px;">
                        ${formatPrice(originalPrice)}
                    </span>
                </div>
            `;
            // Tem giảm giá
            badgeHtml = `
                <span class="discount-badge" style="position:absolute; top:0; right:0; background:#ef4444; color:white; font-size:12px; font-weight:bold; padding:4px 8px; border-bottom-left-radius:8px; z-index:10;">
                    -${i.discount}%
                </span>
            `;
        } else {
            // Giá thường
            priceHtml = `<span style="font-size:18px; font-weight:700; color:#08264a;">${formatPrice(i.price)}</span>`;
        }
        return `
            <div class="menu-item-card">
                <div class="menu-item-image-wrapper" style="position: relative;">
                    <img class="menu-item-image" src="${imgUrl}" onerror="this.src='/images/default-food.png'">
                    ${badgeHtml}
                </div>
                <div class="menu-item-content">
                    <h3 class="menu-item-name">${i.name}</h3>
                    <p class="menu-item-desc" style="font-size:13px; color:#666; margin-bottom:8px;">${i.description || ''}</p>
                    <div class="menu-item-footer" style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: auto;">
                        ${priceHtml}
                        <button class="btn-add" onclick="addToCart('${i.id}')">Thêm</button>
                    </div>
                </div>
            </div>
        `;
    }).join("");
}

function renderCart() {
    const el = document.getElementById("cart-items")

    if (!cart.length) {
        el.innerHTML = "<p>Giỏ hàng trống</p>"
        document.getElementById("cart-count").textContent = "Giỏ hàng trống"
        document.getElementById("cart-total").textContent = "0đ"
        return
    }

    const itemsHtml= cart.map(i => `
        <div class="cart-item">
            <img class="cart-item-image" src="${i.image || '/images/default-food.png'}" onerror="this.src='/images/default-food.png'">
            <div class="cart-item-info">
                <div class="cart-item-name">${i.name}</div>
                <div class="cart-item-price">${formatPrice(i.price * i.quantity)}</div>

                <div class="cart-item-controls">
                    <button class="btn-quantity" onclick="decreaseQty('${i.id}')">−</button>
                    <span class="quantity-text">${i.quantity}</span>
                    <button class="btn-quantity" onclick="increaseQty('${i.id}')">+</button>

                    <button class="btn-remove" onclick="removeItem('${i.id}')">✕</button>
                </div>
            </div>
        </div>
    `).join("")
    //Ô nhập ghi chú
    const noteHtml = `
        <div class="cart-note-section">
            <label for="order-note">📝 Ghi chú món ăn:</label>
            <textarea id="order-note" placeholder="Ví dụ: Không hành, ít cay, nước sốt để riêng..."></textarea>
        </div>
    `;

    // 3. Gộp lại và hiển thị
    el.innerHTML = itemsHtml + noteHtml;
    updateCartTotal()
}
function updateCartTotal() {
    const total = cart.reduce((sum, i) => sum + i.price * i.quantity, 0)
    document.getElementById("cart-total").textContent = formatPrice(total)
    document.getElementById("cart-count").textContent = `${cart.length} món`
}

/* ========= CART ========= */
function addToCart(id) {
    const item = ServerData.getMenuItem(id);
    if (!item) return;

    // Tính giá thực tế để lưu vào giỏ
    let finalPrice = item.price;
    if (item.discount && item.discount > 0) {
        finalPrice = item.price * (1 - item.discount / 100);
    }
    const exist = cart.find(i => String(i.id) === String(id));

    if (exist) {
        exist.quantity++;
    } else {
        // Lưu finalPrice vào giỏ thay vì giá gốc
        cart.push({...item, price: finalPrice, quantity: 1});
    }

    Storage.saveCart(null, cart); // Hoặc Storage.saveCart(cart) đối với Guest
    updateCartBadge();
    renderCart();
}

function updateCartBadge() {
    const badge = document.getElementById("cart-badge")
    if (badge) {
        const total = cart.reduce((s, i) => s + i.quantity, 0)
        badge.textContent = total
        badge.style.display = total ? "flex" : "none"
    }
}

function toggleCart() {
    document.getElementById("cart-overlay").classList.toggle("active")
    document.getElementById("cart-sidebar").classList.toggle("active")
}

/* ========= ORDER ========= */
async function placeOrder() {
    if (!cart.length) {
        alert("Giỏ hàng trống!");
        return;
    }
    if (!tableId) {
        alert("Vui lòng quét lại mã QR tại bàn."); return;
    }
    const noteInput = document.getElementById("order-note");
    const noteValue = noteInput ? noteInput.value.trim() : "";
    // Chuẩn bị dữ liệu gửi (UserOrderRequest)
    const payload = {
        restaurantId: parseInt(restaurantId),
        tableId: parseInt(tableId),
        note: noteValue,
        items: cart.map(i => ({ productId: parseInt(i.id), quantity: i.quantity }))
    };
    const btn = document.querySelector(".cart-footer button");
    btn.innerText = "Đang gửi...";
    btn.disabled = true;
    try {
        const res = await fetch("/api/user/menu/order", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            alert("Đặt món thành công! Vui lòng đợi nhân viên.");
            cart = [];
            Storage.clearCart(); // Xóa cart sau khi đặt xong
            renderCart();
            updateCartBadge();
            toggleCart();
        } else {
            const txt = await res.text();
            if(res.status === 401) window.location.href = "/user/login";
            else alert("Lỗi: " + txt);
        }
    } catch (e) {
        console.error(e);
        alert("Lỗi kết nối server!");
    } finally {
        btn.innerText = "Đặt món";
        btn.disabled = false;
    }
}

function increaseQty(id) {
    const item = cart.find(i => i.id === id)
    if (!item) return
    item.quantity++
    Storage.saveCart(null, cart)
    updateCartBadge()
    renderCart()
}

function decreaseQty(id) {
    const item = cart.find(i => i.id === id)
    if (!item) return
    item.quantity--
    if (item.quantity <= 0) {
        cart = cart.filter(i => i.id !== id)
    }
    Storage.saveCart(null, cart)
    updateCartBadge()
    renderCart()
}

function removeItem(id) {
    cart = cart.filter(i => i.id !== id)
    Storage.saveCart(null, cart)
    updateCartBadge()
    renderCart()
}
