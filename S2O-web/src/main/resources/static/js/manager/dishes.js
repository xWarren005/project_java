let dishesData = [];
let currentDishId = null;

// Danh sách danh mục cố định
const CATEGORIES_HARDCODED = [
    "Món chính",
    "Tráng miệng",
    "Khai vị",
    "Đồ uống"
];

// Ảnh mặc định nếu món ăn không có ảnh
const DEFAULT_IMG_BASE64 = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiB2aWV3Qm94PSIwIDAgMTUwIDE1MCI+PHJlY3Qgd2lkdGg9IjE1MCIgaGVpZ2h0PSIxNTAiIGZpbGw9IiNlZWVlZWUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjIwIiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+Tk8gSU1BR0U8L3RleHQ+PC9zdmc+';

document.addEventListener("DOMContentLoaded", () => {
    // 1. Tải danh sách món ăn
    fetchDishes();

    // 2. Gán sự kiện cho nút "Thêm Món"
    const btnAdd = document.getElementById("btn-add-dish");
    if (btnAdd) {
        btnAdd.addEventListener("click", () => openModal());
    }

    // 3. Render menu header (nếu có dùng file header.js chung)
    if (typeof renderMenu === "function") renderMenu('dishes');
});

// ===================================================
// 1. API & DỮ LIỆU
// ===================================================

function fetchDishes() {
    fetch('/api/manager/dishes')
        .then(res => res.json())
        .then(data => {
            dishesData = data;
            renderDishesGrid();
        })
        .catch(err => {
            console.error("Lỗi tải món:", err);
            document.getElementById("dishes-container").innerHTML = "<p>Lỗi kết nối server!</p>";
        });
}

// ===================================================
// 2. UI RENDER (HIỂN THỊ)
// ===================================================

function renderDishesGrid() {
    const grid = document.getElementById("dishes-container");
    if (!grid) return;

    grid.innerHTML = "";

    if (dishesData.length === 0) {
        grid.innerHTML = "<p>Chưa có món ăn nào.</p>";
        return;
    }

    dishesData.forEach(dish => {
        const catName = dish.category ? dish.category.name : "Chưa phân loại";

        // --- LOGIC MỚI: Xử lý hiển thị giá ---
        let priceHtml = '';
        if (dish.discount > 0) {
            // Tính giá sau giảm: Giá gốc * (100 - %giảm) / 100
            const discountedPrice = dish.price * (1 - dish.discount / 100);

            priceHtml = `
                <span class="price-new">${formatCurrency(discountedPrice)}</span>
                <span class="price-old">${formatCurrency(dish.price)}</span>
                <span class="discount-tag">-${dish.discount}%</span>
            `;
        } else {
            // Không giảm giá thì hiện giá gốc bình thường
            priceHtml = `<span class="price">${formatCurrency(dish.price)}</span>`;
        }
        // -------------------------------------

        const card = document.createElement("div");
        card.className = "dish-card";

        card.innerHTML = `
            <div class="card-img-top">
                <img src="${dish.imageUrl || DEFAULT_IMG_BASE64}" alt="${dish.name}">
            </div>
            <div class="card-body">
                <h4>${dish.name}</h4>
                
                <div class="price-row">
                    ${priceHtml}
                </div>
                
                <p class="desc">${dish.description || ''}</p>
                <div class="tags">
                    <span class="badge ${dish.isAvailable ? 'available' : 'unavailable'}">
                        ${dish.isAvailable ? 'Đang bán' : 'Ngưng bán'}
                    </span>
                    <span class="category-tag">${catName}</span>
                </div>
            </div>
            <div class="card-actions">
                <button class="btn-edit" onclick="editDish(${dish.id})"><i class="fa-solid fa-pen"></i> Sửa</button>
                <button class="btn-delete" onclick="deleteDish(${dish.id})"><i class="fa-solid fa-trash"></i> Xóa</button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function formatCurrency(val) {
    return new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(val);
}

// ===================================================
// 3. MODAL LOGIC (MỞ/ĐÓNG POPUP)
// ===================================================

// Hàm mở Modal (dùng cho cả Thêm mới và Sửa)
function openModal(dish = null) {
    // SỬA: Dùng đúng ID 'modal-overlay'
    const modal = document.getElementById("modal-overlay");
    if (!modal) return;

    currentDishId = dish ? dish.id : null;

    // Cập nhật tiêu đề modal
    const modalTitle = document.getElementById("modal-title");
    if (modalTitle) modalTitle.innerText = dish ? "Cập Nhật Món Ăn" : "Thêm Món Ăn Mới";

    // 1. Reset/Fill Form Data
    document.getElementById("inp-name").value = dish ? dish.name : "";
    document.getElementById("inp-price").value = dish ? dish.price : "";
    document.getElementById("inp-discount").value = dish ? (dish.discount || 0) : 0;
    document.getElementById("inp-desc").value = dish ? (dish.description || "") : "";
    document.getElementById("inp-status").checked = dish ? dish.isAvailable : true;

    // 2. Reset hình ảnh
    document.getElementById("inp-image-file").value = ""; // Reset input file
    const imgPreview = document.getElementById("img-preview");
    if (dish && dish.imageUrl) {
        imgPreview.src = dish.imageUrl;
        imgPreview.style.display = "block";
    } else {
        imgPreview.src = "";
        imgPreview.style.display = "none";
    }

    // 3. Render dropdown Category
    const select = document.getElementById("inp-category");
    select.innerHTML = "";
    CATEGORIES_HARDCODED.forEach(catName => {
        const option = document.createElement("option");
        option.value = catName;
        option.innerText = catName;
        if (dish && dish.category && dish.category.name === catName) {
            option.selected = true;
        }
        select.appendChild(option);
    });

    // 4. Hiển thị Modal (Xóa class hidden)
    modal.classList.remove("hidden");
}

function closeModal() {
    const modal = document.getElementById("modal-overlay");
    if (modal) modal.classList.add("hidden");
}

// Hàm xem trước ảnh (Được gọi từ onchange trong HTML)
function previewImage(event) {
    const input = event.target;
    const preview = document.getElementById("img-preview");

    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = "block";
        }
        reader.readAsDataURL(input.files[0]);
    } else {
        preview.src = "";
        preview.style.display = "none";
    }
}

// ===================================================
// 4. XỬ LÝ FORM SUBMIT (THÊM / SỬA)
// ===================================================

// Hàm này được gọi từ onsubmit="handleFormSubmit(event)" trong HTML
async function handleFormSubmit(event) {
    event.preventDefault(); // Ngăn trình duyệt load lại trang

    const name = document.getElementById("inp-name").value;
    const price = document.getElementById("inp-price").value;
    const desc = document.getElementById("inp-desc").value;
    const catName = document.getElementById("inp-category").value;
    const isAvail = document.getElementById("inp-status").checked;
    const discount = document.getElementById("inp-discount").value;
    const fileInput = document.getElementById("inp-image-file"); // SỬA: ID đúng

    // FormData để gửi cả file và text
    const formData = new FormData();
    formData.append("name", name);
    formData.append("price", price);
    formData.append("description", desc);
    formData.append("categoryName", catName);
    formData.append("isAvailable", isAvail);
    formData.append("discount", discount);

    if (fileInput.files.length > 0) {
        formData.append("imageFile", fileInput.files[0]);
    }

    // Xác định URL và Method
    let url = "/api/manager/dishes";
    let method = "POST";

    if (currentDishId) {
        url = `/api/manager/dishes/${currentDishId}`;
        method = "PUT";
    }

    // Gọi API
    const btnSave = document.getElementById("btn-save");
    const originalText = btnSave.innerText;
    btnSave.innerText = "Đang lưu...";
    btnSave.disabled = true;

    try {
        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (response.ok) {
            alert(currentDishId ? "Cập nhật thành công!" : "Thêm mới thành công!");
            closeModal();
            fetchDishes(); // Load lại danh sách
        } else {
            const txt = await response.text();
            alert("Lỗi server: " + txt);
        }
    } catch (err) {
        console.error(err);
        alert("Không thể kết nối đến server!");
    } finally {
        btnSave.innerText = originalText;
        btnSave.disabled = false;
    }
}

// ===================================================
// 5. EDIT & DELETE
// ===================================================

function editDish(id) {
    const dish = dishesData.find(d => d.id === id);
    if (dish) {
        openModal(dish);
    } else {
        alert("Không tìm thấy thông tin món ăn này!");
    }
}

function deleteDish(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa món này không?")) return;

    fetch(`/api/manager/dishes/${id}`, {method: 'DELETE'})
        .then(res => {
            if (res.ok) {
                alert("Đã xóa món ăn.");
                fetchDishes();
            } else {
                alert("Không thể xóa (Có thể món đang có trong đơn hàng).");
            }
        })
        .catch(err => console.error(err));
}