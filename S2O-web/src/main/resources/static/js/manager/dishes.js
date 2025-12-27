let dishesData = [];
let categoriesData = [];
const DEFAULT_IMG_BASE64 = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNTAiIGhlaWdodD0iMTUwIiB2aWV3Qm94PSIwIDAgMTUwIDE1MCI+PHJlY3Qgd2lkdGg9IjE1MCIgaGVpZ2h0PSIxNTAiIGZpbGw9IiNlZWVlZWUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjIwIiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+Tk8gSU1BR0U8L3RleHQ+PC9zdmc+';

document.addEventListener("DOMContentLoaded", () => {
    fetchCategories();
    fetchDishes();
    const btnAdd = document.getElementById("btn-add-dish");
    if (btnAdd) btnAdd.addEventListener("click", () => openModal());
    if (typeof renderMenu === "function") renderMenu('dishes');
});

// ===================================================
// API
// ===================================================
function fetchCategories() {
    fetch('/api/manager/categories')
        .then(res => res.json())
        .then(data => {
            categoriesData = data;
            const select = document.getElementById("inp-category");
            select.innerHTML = '<option value="">-- Chọn danh mục --</option>';
            data.forEach(cat => {
                const opt = document.createElement("option");
                opt.value = cat.id;
                opt.textContent = cat.name;
                select.appendChild(opt);
            });
        }).catch(err => console.error("Lỗi tải danh mục:", err));
}

function fetchDishes() {
    fetch('/api/manager/dishes')
        .then(res => res.json())
        .then(data => {
            dishesData = data;
            renderDishesGrid();
        }).catch(err => console.error("Lỗi tải danh sách món:", err));
}

// ===================================================
// HELPER
// ===================================================
function getFolderName(categoryName) {
    if (!categoryName) return "other";
    let str = categoryName.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    str = str.replace(/\s+/g, '-').toLowerCase();
    if (str === "mon-chinh") return "Mon-chinh";
    if (str === "khai-vi") return "Khai-vi";
    if (str === "do-uong") return "Do-uong";
    return str;
}

function resolveImageUrl(dish) {
    if (!dish.imageUrl) return DEFAULT_IMG_BASE64;
    if (dish.imageUrl.startsWith("http")) return dish.imageUrl;
    if (dish.imageUrl.startsWith("/")) return dish.imageUrl;
    if (dish.imageUrl.includes("uploads/")) return "/" + dish.imageUrl;
    let folder = getFolderName(dish.category ? dish.category.name : "other");
    return `/image/${folder}/${dish.imageUrl}`;
}

// ===================================================
// RENDER
// ===================================================
function renderDishesGrid() {
    const container = document.getElementById("dishes-container");
    if (!dishesData || dishesData.length === 0) {
        container.innerHTML = '<p>Chưa có món ăn nào.</p>';
        return;
    }

    container.innerHTML = dishesData.map(dish => {
        const discount = ('discount' in dish) ? parseFloat(dish.discount) : 0;
        const finalPrice = dish.price * (1 - discount / 100);
        const formattedPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(dish.price);
        const formattedFinalPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(finalPrice);
        const statusClass = dish.isAvailable ? 'status-active' : 'status-inactive';
        const catName = dish.category ? dish.category.name : (dish.categoryName || "Unknown");
        const imageSrc = resolveImageUrl(dish);

        return `
            <div class="dish-card">
                <div class="dish-image">
                    <img src="${imageSrc}" alt="${dish.name}" onerror="this.src='${DEFAULT_IMG_BASE64}'">
                    <span class="category-badge">${catName}</span>
                </div>
                <div class="dish-info">
                    <h4 class="dish-name">${dish.name}</h4>
                    <div class="price-row">
                        ${discount > 0
            ? `<span class="current-price">${formattedFinalPrice}</span>
               <span class="old-price">${formattedPrice}</span>`
            : `<span class="current-price">${formattedPrice}</span>`}
                    </div>
                    <span class="dish-status ${statusClass}">${dish.isAvailable ? 'Đang bán' : 'Hết hàng'}</span>
                </div>
                <div class="card-actions">
                    <button class="btn-edit" onclick="editDish(${dish.id})"><i class="fa-solid fa-pen"></i></button>
                    <button class="btn-delete" onclick="deleteDish(${dish.id})"><i class="fa-solid fa-trash"></i></button>
                </div>
            </div>
        `;
    }).join('');
}

// ===================================================
// ẢNH & FORM
// ===================================================
function previewImage(event) {
    const file = event.target.files[0];
    const preview = document.getElementById("img-preview");
    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.style.display = "block";
    } else preview.style.display = "none";
}

function openModal(dish = null) {
    const modal = document.getElementById("modal-overlay");
    const title = document.getElementById("modal-title");
    const form = document.getElementById("dish-form");
    const preview = document.getElementById("img-preview");

    form.reset();
    document.getElementById("inp-image-file").value = "";
    document.getElementById("inp-discount").value = 0;

    if (dish) {
        title.textContent = "Cập Nhật Món";
        document.getElementById("dish-id").value = dish.id;
        document.getElementById("inp-name").value = dish.name;
        document.getElementById("inp-price").value = dish.price;
        document.getElementById("inp-desc").value = dish.description;
        document.getElementById("inp-status").checked = dish.isAvailable;
        document.getElementById("inp-category").value = dish.category ? dish.category.id : "";
        document.getElementById("inp-discount").value = dish.discount || 0;

        preview.src = resolveImageUrl(dish);
        preview.style.display = "block";
    } else title.textContent = "Thêm Món Mới";

    modal.style.display = "flex";
    modal.classList.remove("hidden");
}

function closeModal() {
    document.getElementById("modal-overlay").style.display = "none";
}

// ===================================================
// SUBMIT FORM
// ===================================================
async function handleFormSubmit(event) {
    event.preventDefault();

    const dishId = document.getElementById("dish-id").value;
    const fileInput = document.getElementById("inp-image-file");
    const discount = parseFloat(document.getElementById("inp-discount").value) || 0;

    const formData = new FormData();
    formData.append("name", document.getElementById("inp-name").value);
    formData.append("price", document.getElementById("inp-price").value);
    formData.append("description", document.getElementById("inp-desc").value);
    formData.append("categoryId", document.getElementById("inp-category").value);
    formData.append("isAvailable", document.getElementById("inp-status").checked);
    formData.append("discount", discount);

    if (fileInput.files.length > 0) {
        formData.append("imageFile", fileInput.files[0]);
    }

    const url = dishId ? `/api/manager/dishes/${dishId}` : '/api/manager/dishes';
    const method = dishId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, { method, body: formData });
        if (response.ok) {
            alert("Lưu thành công!");

            // Cập nhật dishesData ngay lập tức để render lại mà không cần fetch
            const dishData = {
                id: dishId ? parseInt(dishId) : Date.now(),
                name: document.getElementById("inp-name").value,
                price: parseFloat(document.getElementById("inp-price").value),
                description: document.getElementById("inp-desc").value,
                isAvailable: document.getElementById("inp-status").checked,
                category: categoriesData.find(c => c.id == document.getElementById("inp-category").value),
                discount: discount,
                imageUrl: fileInput.files.length > 0 ? URL.createObjectURL(fileInput.files[0]) : undefined
            };

            if (dishId) {
                const index = dishesData.findIndex(d => d.id == dishId);
                if (index !== -1) dishesData[index] = dishData;
            } else dishesData.push(dishData);

            renderDishesGrid();
            closeModal();
        } else alert("Lỗi: " + await response.text());
    } catch (err) {
        alert("Lỗi kết nối!");
        console.error(err);
    }
}

// ===================================================
// EDIT / DELETE
// ===================================================
function editDish(id) {
    const dish = dishesData.find(d => d.id === id);
    if (dish) openModal(dish);
}

function deleteDish(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa món này không?")) return;
    fetch(`/api/manager/dishes/${id}`, { method: 'DELETE' })
        .then(res => res.ok ? fetchDishes() : alert("Không thể xóa món ăn"))
        .catch(() => alert("Lỗi kết nối!"));
}
