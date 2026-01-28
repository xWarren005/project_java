/* ============================================================
   GUEST MENU JS - LOGIC CHO KHÁCH VÃNG LAI
   ============================================================ */

/* ========= 0. SETUP & GLOBALS ========= */
// Đọc thông tin từ LocalStorage (Được lưu từ trang Welcome)
const TABLE_ID = localStorage.getItem("currentTableId");
const RESTAURANT_ID = localStorage.getItem("currentRestaurant") || 1; // Mặc định 1 nếu thiếu
const TABLE_NAME = localStorage.getItem("currentTableName") || "Bàn của bạn";

// Kiểm tra bảo mật: Nếu không có ID bàn -> Đá về trang Welcome
if (!TABLE_ID) {
  alert("Không tìm thấy thông tin bàn. Vui lòng quét lại mã QR.");
  window.location.href = "/";
}

// Hiển thị tên bàn lên giao diện
document.addEventListener("DOMContentLoaded", () => {
  const tableEl = document.getElementById("table-number");
  if(tableEl) tableEl.textContent = TABLE_NAME;

  // Fix tên bàn ở tab hóa đơn luôn
  const invoiceTableEl = document.getElementById("invoice-table");
  if(invoiceTableEl) invoiceTableEl.textContent = TABLE_NAME;
});


/* ========= 1. COMMON / UTIL ========= */
function formatPrice(price) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
}

/* ========= 2. NAVIGATION & TABS ========= */
function switchTab(tab) {
  // 1. UI Active classes
  document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
  document.querySelector(`.tab[data-tab="${tab}"]`)?.classList.add("active");
  document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));
  document.getElementById(`tab-${tab}`)?.classList.add("active");

  // 2. LOGIC LOAD DỮ LIỆU TƯƠNG ỨNG (Guest)
  // Các hàm renderGuest... nằm ở các file js con (guest-orders.js, guest-invoice.js...)
  if (tab === 'orders') {
    if (typeof renderGuestOrders === "function") renderGuestOrders();
  }
  if (tab === 'invoice') {
    if (typeof renderGuestInvoice === "function") renderGuestInvoice();
  }
  if (tab === 'payment') {
    if (typeof renderGuestPayment === "function") renderGuestPayment();
  }
}

/* ========= 3. STORAGE (LOCALSTORAGE) ========= */
const Storage = {
  // Key giỏ hàng riêng biệt theo ID bàn để tránh xung đột
  getCartKey() {
    return `guest_cart_${TABLE_ID}`;
  },
  getCart() {
    return JSON.parse(localStorage.getItem(this.getCartKey()) || "[]");
  },
  saveCart(cart) {
    localStorage.setItem(this.getCartKey(), JSON.stringify(cart));
  },
  clearCart() {
    localStorage.removeItem(this.getCartKey());
  }
};

/* ========= 4. SERVER DATA ========= */
let ServerData = {
  categories: [],
  menuItems: [],

  getCategories() { return this.categories; },

  getMenuItems(category = "all", search = "") {
    let items = this.menuItems;
    if (category !== "all") {
      // So sánh ID dạng string để an toàn
      items = items.filter(i => String(i.category) === String(category));
    }
    if (search) {
      const q = search.toLowerCase();
      items = items.filter(i => i.name.toLowerCase().includes(q));
    }
    return items;
  },

  getMenuItem(id) {
    // So sánh lỏng (==) để tránh lỗi string/number
    return this.menuItems.find(i => i.id == id);
  }
};

/* ========= 5. MENU LOGIC ========= */
let selectedCategory = "all";
let cart = [];

/* INIT */
document.addEventListener("DOMContentLoaded", async () => {
  // Load giỏ hàng cũ
  cart = Storage.getCart();
  updateCartBadge();
  renderCart(); // Render cart để cập nhật tổng tiền ban đầu

  // Gọi API lấy Menu
  await fetchGuestMenuData();
});

/* --- HÀM GỌI API GUEST --- */
async function fetchGuestMenuData() {
  try {
    // API: /api/guest/menu
    const res = await fetch(`/api/guest/menu?restaurantId=${RESTAURANT_ID}`);

    if (res.ok) {
      const data = await res.json();

      // Map dữ liệu API vào ServerData
      ServerData.categories = data.categories;
      ServerData.menuItems = data.menuItems;

      loadCategories();
      loadMenuItems();
    } else {
      console.error("Lỗi HTTP:", res.status);
      document.getElementById("menu-items").innerHTML = "<div class='empty-state'>Lỗi tải thực đơn</div>";
    }
  } catch (e) {
    console.error("Lỗi tải menu:", e);
    document.getElementById("menu-items").innerHTML = "<div class='empty-state'>Lỗi kết nối server</div>";
  }
}

/* ========= RENDER UI ========= */
function loadCategories() {
  const el = document.getElementById("categories");
  if(!el) return;

  el.innerHTML = ServerData.getCategories().map(c => `
        <button class="category-btn ${String(c.id) === String(selectedCategory) ? "active" : ""}" 
            onclick="selectCategory('${c.id}')">${c.name}</button>
    `).join("");
}

function selectCategory(id) {
  selectedCategory = id;
  loadCategories();
  loadMenuItems();
}

function searchMenu() { loadMenuItems(); }

function loadMenuItems() {
  const searchInput = document.getElementById("search-input");
  const search = searchInput ? searchInput.value : "";

  const el = document.getElementById("menu-items");
  const items = ServerData.getMenuItems(selectedCategory, search);

  if (!items.length) {
    el.innerHTML = '<div class="empty-state">Không có món ăn phù hợp</div>';
    return;
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

/* ========= CART LOGIC ========= */
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
function increaseQty(id) {
  const item = cart.find(i => i.id == id);
  if (!item) return;
  item.quantity++;
  Storage.saveCart(cart);
  renderCart();
}

function decreaseQty(id) {
  const item = cart.find(i => i.id == id);
  if (!item) return;
  item.quantity--;
  if (item.quantity <= 0) {
    cart = cart.filter(i => i.id != id);
  }
  Storage.saveCart(cart);
  updateCartBadge();
  renderCart();
}

function removeItem(id) {
  cart = cart.filter(i => i.id != id);
  Storage.saveCart(cart);
  updateCartBadge();
  renderCart();
}

function renderCart() {
  const el = document.getElementById("cart-items");
  if(!el) return;

  if (!cart.length) {
    el.innerHTML = "<p style='text-align:center; padding:20px; color:#999;'>Giỏ hàng trống</p>";
    document.getElementById("cart-count").textContent = "0 món";
    document.getElementById("cart-total").textContent = "0đ";
    updateCartBadge();
    return;
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
    `).join("");
  //Ô nhập ghi chú
  const noteHtml = `
        <div class="cart-note-section">
            <label for="order-note">📝 Ghi chú món ăn:</label>
            <textarea id="order-note" placeholder="Ví dụ: Không hành, ít cay, nước sốt để riêng..."></textarea>
        </div>
    `;
  // 3. Gộp lại và hiển thị
  el.innerHTML = itemsHtml + noteHtml;
  const total = cart.reduce((sum, i) => sum + i.price * i.quantity, 0);
  document.getElementById("cart-total").textContent = formatPrice(total);
  document.getElementById("cart-count").textContent = `${cart.length} món`;
  updateCartBadge();
}

function updateCartBadge() {
  const badge = document.getElementById("cart-badge");
  if (badge) {
    const total = cart.reduce((s, i) => s + i.quantity, 0);
    badge.textContent = total;
    badge.style.display = total ? "flex" : "none";
  }
}

function toggleCart() {
  const profileSidebar = document.getElementById("profile-sidebar");
  const profileOverlay = document.getElementById("profile-overlay");
  if (profileSidebar && profileSidebar.classList.contains("active")) {
    profileSidebar.classList.remove("active");
    profileOverlay.classList.remove("active");
  }
  document.getElementById("cart-overlay").classList.toggle("active");
  document.getElementById("cart-sidebar").classList.toggle("active");
}
function toggleProfile() {
  const profileSidebar = document.getElementById("profile-sidebar");
  const profileOverlay = document.getElementById("profile-overlay");

  if (!profileSidebar || !profileOverlay) return;

  // Nếu Cart đang mở thì đóng nó lại trước
  const cartSidebar = document.getElementById("cart-sidebar");
  const cartOverlay = document.getElementById("cart-overlay");
  if (cartSidebar && cartSidebar.classList.contains("active")) {
    cartSidebar.classList.remove("active");
    cartOverlay.classList.remove("active");
  }

  // Toggle Profile
  profileOverlay.classList.toggle("active");
  profileSidebar.classList.toggle("active");
}
/* ========= 6. PLACE ORDER (GỬI BẾP) ========= */
async function placeOrder() {
  if (!cart.length) {
    alert("Giỏ hàng đang trống!");
    return;
  }
  if (!TABLE_ID) {
    alert("Lỗi: Mất thông tin bàn. Vui lòng quét lại QR.");
    return;
  }
// 1. 🔥 LẤY GHI CHÚ
  const noteInput = document.getElementById("order-note");
  const noteValue = noteInput ? noteInput.value.trim() : "";
  // Chuẩn bị Payload khớp với GuestOrderRequest DTO
  const payload = {
    restaurantId: parseInt(RESTAURANT_ID),
    tableId: parseInt(TABLE_ID),
    note:noteValue, // Có thể thêm input note sau này
    items: cart.map(i => ({
      productId: parseInt(i.id),
      quantity: i.quantity,
    }))
  };

  // UI Loading
  const btn = document.querySelector(".cart-footer button");
  const oldText = btn.innerText;
  btn.innerText = "Đang gửi...";
  btn.disabled = true;

  try {
    // API GUEST ORDER
    const res = await fetch("/api/guest/order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      alert("Đã gửi đơn thành công! Vui lòng chờ xác nhận.");
      // Reset Cart
      cart = [];
      Storage.clearCart();
      renderCart();
      updateCartBadge();
      toggleCart(); // Đóng sidebar

      // Chuyển sang tab theo dõi
      switchTab('orders');
    } else {
      const txt = await res.text();
      alert("Lỗi đặt món: " + txt);
    }
  } catch (e) {
    console.error(e);
    alert("Lỗi kết nối server!");
  } finally {
    btn.innerText = oldText;
    btn.disabled = false;
  }
}