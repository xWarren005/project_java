// Hàm này chạy khi bấm nút
function saveAndGo(url) {
    // 1. Lấy dữ liệu từ thẻ input ẩn
    const resId = document.getElementById("qr-res-id").value;
    const tableName = document.getElementById("qr-table-name").value;
    const tableId = document.getElementById("qr-table-id").value;
    // 2. Lưu vào LocalStorage (Bộ nhớ trình duyệt)
    // Để trang Menu/Login lát nữa có thể đọc được
    console.log(`Setup bàn: ID=${tableId}, Name=${tableName}`);
    if (tableId) {
        localStorage.setItem("currentTableId", tableId);

        // Khởi tạo giỏ hàng rỗng cho bàn này nếu chưa có (tránh lỗi null)
        const cartKey = `guest_cart_${tableId}`;
        if (!localStorage.getItem(cartKey)) {
            localStorage.setItem(cartKey, "[]");
        }
    }
    if (tableName) {
        localStorage.setItem("currentTable", tableName);
    }
    if(resId) {
        localStorage.setItem("currentRestaurant", resId);
    }

    // 3. Chuyển trang
    window.location.href = url;
}

// Tự động lưu ngay khi trang vừa tải xong (Dự phòng)
document.addEventListener("DOMContentLoaded", () => {
    const tableId = document.getElementById("qr-table-id").value;
    const resId = document.getElementById("qr-res-id").value;
    const tableName = document.getElementById("qr-table-name").value;
    if (tableName) {
        localStorage.setItem("currentTable", tableName);
    }
    if (tableId) {
        localStorage.setItem("currentTableId", tableId);
    }
    if(resId) {
        localStorage.setItem("currentRestaurant", resId);
    }
});