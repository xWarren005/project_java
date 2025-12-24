// Biến toàn cục chứa danh sách món ăn
let dishesData = [];

// --- 1. KHỞI TẠO KHI TRANG LOAD ---
document.addEventListener("DOMContentLoaded", () => {
    console.log("Loading Dishes Page...");

    // Gọi hàm lấy dữ liệu
    fetchDishes();

    // Gán sự kiện cho nút "Thêm Món"
    document.getElementById("btn-add-dish").addEventListener("click", () => {
        openModal(); // Mở modal trống để thêm mới
    });

    // Render Menu (Sidebar)
    if (typeof renderMenu === "function") {
        renderMenu('dishes');
    }
});

// --- 2. GỌI API LẤY DỮ LIỆU ---
function fetchDishes() {
    // Giả sử API Java trả về List<ProductDTO>
    fetch('/api/manager/dishes')
        .then(response => {
            // Nếu chưa có API, dùng dữ liệu giả để test giao diện (comment đoạn này lại khi có API thật)
            /*
            if (!response.ok) return mockData();
            */
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            dishesData = data;
            renderDishesGrid();
        })
        .catch(error => {
            console.error('Error fetching dishes:', error);
            document.getElementById("dishes-container").innerHTML = '<p class="error">Không tải được dữ liệu.</p>';
        });
}

// --- 3. HIỂN THỊ DỮ LIỆU LÊN GIAO DIỆN ---
function renderDishesGrid() {
    const container = document.getElementById("dishes-container");

    if (!dishesData || dishesData.length === 0) {
        container.innerHTML = '<p>Chưa có món ăn nào.</p>';
        return;
    }

    container.innerHTML = dishesData.map(dish => {
        // Xử lý hiển thị trạng thái (true/false từ DB -> Text)
        const statusClass = dish.isAvailable ? 'status-active' : 'status-inactive';
        const statusText = dish.isAvailable ? 'Đang phục vụ' : 'Hết hàng';

        // Format tiền tệ VNĐ
        const formattedPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(dish.price);

        return `
            <div class="dish-card">
                <div class="dish-image">
                    <img src="${dish.imageUrl || '/images/default-dish.png'}" alt="${dish.name}">
                    <span class="category-badge">${dish.categoryName || 'Món ăn'}</span>
                </div>
                <div class="dish-info">
                    <h4 class="dish-name">${dish.name}</h4>
                    <p class="dish-desc">${dish.description || 'Chưa có mô tả'}</p>
                    <div class="dish-meta">
                        <span class="dish-price">${formattedPrice}</span>
                        <span class="dish-status ${statusClass}">${statusText}</span>
                    </div>
                </div>
                <div class="dish-actions">
                    <button class="btn-edit" onclick="editDish(${dish.id})"><i class="fa-solid fa-pen"></i> Sửa</button>
                    <button class="btn-delete" onclick="deleteDish(${dish.id})"><i class="fa-solid fa-trash"></i> Xóa</button>
                </div>
            </div>
        `;
    }).join('');
}

// --- 4. CÁC HÀM XỬ LÝ MODAL (Thêm/Sửa) ---

// Mở Modal
function openModal(dish = null) {
    const modal = document.getElementById("modal-overlay");
    const title = document.getElementById("modal-title");

    // Reset form
    document.getElementById("dish-form").reset();

    if (dish) {
        // Chế độ Sửa: Điền dữ liệu cũ vào form
        title.textContent = "Cập Nhật Món Ăn";
        document.getElementById("dish-id").value = dish.id;
        document.getElementById("inp-name").value = dish.name;
        document.getElementById("inp-price").value = dish.price;
        document.getElementById("inp-desc").value = dish.description;
        document.getElementById("inp-status").checked = dish.isAvailable;
        // document.getElementById("inp-category").value = dish.categoryId; // Cần map đúng value select
    } else {
        // Chế độ Thêm mới
        title.textContent = "Thêm Món Ăn Mới";
        document.getElementById("dish-id").value = "";
    }

    modal.classList.remove("hidden");
    modal.style.display = "flex"; // CSS của bạn có thể dùng display: flex
}

// Đóng Modal
function closeModal() {
    const modal = document.getElementById("modal-overlay");
    modal.classList.add("hidden");
    modal.style.display = "none";
}

// Xử lý khi bấm nút Lưu
function handleFormSubmit(event) {
    event.preventDefault(); // Chặn reload trang

    const dishId = document.getElementById("dish-id").value;
    const newDish = {
        name: document.getElementById("inp-name").value,
        price: parseFloat(document.getElementById("inp-price").value),
        description: document.getElementById("inp-desc").value,
        isAvailable: document.getElementById("inp-status").checked,
        // categoryId: ... lấy từ select
    };

    console.log("Saving dish:", newDish, "ID:", dishId);

    // TODO: Gọi API POST (Thêm) hoặc PUT (Sửa) về server Java tại đây
    // Sau khi thành công -> fetchDishes() lại -> closeModal()
    alert("Chức năng đang phát triển: Gửi dữ liệu về Java");
    closeModal();
}

// Hàm chuẩn bị dữ liệu để sửa (gọi từ nút Sửa trên giao diện)
function editDish(id) {
    const dish = dishesData.find(d => d.id === id);
    if (dish) {
        openModal(dish);
    }
}

// Hàm xóa
function deleteDish(id) {
    if(confirm("Bạn có chắc muốn xóa món này?")) {
        console.log("Delete dish id:", id);
        // TODO: Gọi API DELETE
    }
}

// --- HÀM DỮ LIỆU GIẢ (Dùng khi chưa có Backend) ---
function mockData() {
    return [
        { id: 1, name: "Phở Bò Đặc Biệt", price: 65000, description: "Nạm, gầu, gân, bò viên", categoryName: "Món Chính", isAvailable: true },
        { id: 2, name: "Nem Rán Hà Nội", price: 120000, description: "Thịt heo, mộc nhĩ, miến", categoryName: "Khai Vị", isAvailable: true },
        { id: 3, name: "Trà Đào Cam Sả", price: 45000, description: "Ly khổng lồ", categoryName: "Đồ Uống", isAvailable: false }
    ];
}