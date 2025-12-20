// --- DATA GIẢ LẬP ---
let dishesData = [
    { id: 1, name: "Phở Bò Đặc Biệt", desc: "Phở bò tái, nạm", price: 65000, oldPrice: null, category: "Món Chính", catColor: "badge-orange", image: null },
    { id: 2, name: "Bún Chả Hà Nội", desc: "Bún chả nướng", price: 49500, oldPrice: 55000, category: "Món Chính", catColor: "badge-orange", image: null },
    { id: 3, name: "Gỏi Cuốn", desc: "Tôm thịt tươi", price: 35000, oldPrice: null, category: "Khai Vị", catColor: "badge-green", image: null },
    { id: 4, name: "Chè Ba Màu", desc: "Đậu đỏ, xanh", price: 25000, oldPrice: null, category: "Tráng Miệng", catColor: "badge-pink", image: null }
];

// --- INIT ---
document.addEventListener("DOMContentLoaded", () => {
    renderDishes();

    // Gán sự kiện nút Thêm Món
    document.getElementById("btn-add-dish").addEventListener("click", () => {
        openModal("add");
    });
});

// --- RENDER FUNCTION ---
function renderDishes() {
    const container = document.getElementById("dishes-container");
    const fmt = (n) => n.toLocaleString('vi-VN') + 'đ';

    container.innerHTML = dishesData.map(d => {
        let priceHtml = `<span class="current-price">${fmt(d.price)}</span>`;
        if (d.oldPrice && d.oldPrice > d.price) {
            priceHtml += `<span class="old-price">${fmt(d.oldPrice)}</span>`;
        }

        // Ảnh giả lập
        const imgHtml = d.image
            ? `<img src="${d.image}" class="dish-img">`
            : `<div style="width:100%;height:100%;background:#f0f0f0;display:flex;align-items:center;justify-content:center;color:#ccc"><i class="fa-solid fa-image fa-2x"></i></div>`;

        return `
            <div class="dish-card">
                <div class="dish-img-box">${imgHtml}</div>
                <h4 class="dish-title">${d.name}</h4>
                <div class="dish-desc">${d.desc || 'Chưa có mô tả'}</div>
                <div><span class="badge ${d.catColor || 'badge-blue'}">${d.category}</span></div>
                <div class="price-row">${priceHtml}</div>
                <div class="card-actions">
                    <button class="btn-edit" onclick="openEditModal(${d.id})"><i class="fa-solid fa-pen"></i> Sửa</button>
                    <button class="btn-delete" onclick="deleteDish(${d.id})"><i class="fa-solid fa-trash"></i> Xóa</button>
                </div>
            </div>
        `;
    }).join('');
}

// --- MODAL LOGIC ---
const modal = document.getElementById("modal-overlay");
const form = document.getElementById("dish-form");

// Mở Modal (Mode: 'add' hoặc object data món ăn)
function openModal(mode, dish = null) {
    modal.classList.remove("hidden");
    const isAdd = mode === 'add';

    // Cập nhật Tiêu đề & Nút
    document.getElementById("modal-title").textContent = isAdd ? "Thêm Món Ăn Mới" : "Chỉnh Sửa Món Ăn";
    document.getElementById("btn-save").textContent = isAdd ? "Thêm Món" : "Lưu Thay Đổi";

    // Reset hoặc điền form
    if (isAdd) {
        form.reset();
        document.getElementById("dish-id").value = "";
    } else {
        document.getElementById("dish-id").value = dish.id;
        document.getElementById("inp-name").value = dish.name;
        document.getElementById("inp-category").value = dish.category;

        // Tính ngược lại giá gốc và % giảm giá (Logic đơn giản)
        let rawPrice = dish.oldPrice ? dish.oldPrice : dish.price;
        let discount = dish.oldPrice ? Math.round((1 - dish.price/dish.oldPrice) * 100) : 0;

        document.getElementById("inp-price").value = rawPrice;
        document.getElementById("inp-discount").value = discount;
        document.getElementById("inp-desc").value = dish.desc;
    }
}

// Helper gọi từ nút Sửa trong HTML
function openEditModal(id) {
    const dish = dishesData.find(d => d.id === id);
    if (dish) openModal("edit", dish);
}

function closeModal() {
    modal.classList.add("hidden");
}

// --- HANDLE SUBMIT (THÊM / SỬA) ---
function handleFormSubmit(e) {
    e.preventDefault();

    // 1. Lấy dữ liệu từ form
    const id = document.getElementById("dish-id").value;
    const name = document.getElementById("inp-name").value;
    const category = document.getElementById("inp-category").value;
    const rawPrice = Number(document.getElementById("inp-price").value);
    const discount = Number(document.getElementById("inp-discount").value);
    const desc = document.getElementById("inp-desc").value;

    // 2. Tính toán giá mới
    let finalPrice = rawPrice;
    let oldPrice = null;

    if (discount > 0) {
        finalPrice = rawPrice * (1 - discount / 100);
        oldPrice = rawPrice;
    }

    // Xác định màu badge theo danh mục
    let catColor = "badge-blue";
    if (category === "Món Chính") catColor = "badge-orange";
    if (category === "Khai Vị") catColor = "badge-green";
    if (category === "Tráng Miệng") catColor = "badge-pink";

    // 3. Logic: Sửa hoặc Thêm
    if (id) {
        // --- SỬA ---
        const index = dishesData.findIndex(d => d.id == id);
        if (index !== -1) {
            dishesData[index] = {
                ...dishesData[index],
                name, category, desc, price: finalPrice, oldPrice, catColor
            };
            alert("Đã cập nhật món ăn!");
        }
    } else {
        // --- THÊM ---
        const newDish = {
            id: Date.now(), // Tạo ID ngẫu nhiên theo thời gian
            name,
            desc,
            price: finalPrice,
            oldPrice,
            category,
            catColor,
            image: null
        };
        dishesData.push(newDish);
        alert("Đã thêm món mới thành công!");
    }

    // 4. Reset & Render lại
    closeModal();
    renderDishes();
}

// --- XÓA MÓN ---
function deleteDish(id) {
    if (confirm("Bạn có chắc chắn muốn xóa món này không?")) {
        dishesData = dishesData.filter(d => d.id !== id);
        renderDishes();
    }
}