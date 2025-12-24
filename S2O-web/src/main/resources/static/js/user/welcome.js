// Hàm này chạy khi bấm nút
function saveAndGo(url) {
    // 1. Lấy dữ liệu từ thẻ input ẩn
    const resId = document.getElementById("qr-res-id").value;
    const tableName = document.getElementById("qr-table-name").value;

    // 2. Lưu vào LocalStorage (Bộ nhớ trình duyệt)
    // Để trang Menu/Login lát nữa có thể đọc được
    if (tableName) {
        localStorage.setItem("currentTable", tableName);
    }
    if(resId) {
        localStorage.setItem("currentRestaurant", resId);
    }
    localStorage.setItem(`cart_${tableName}`, localStorage.getItem(`cart_${tableName}`) || "[]");
    console.log("Đã lưu thông tin bàn:", tableName);

    // 3. Chuyển trang
    window.location.href = url;
}

// Tự động lưu ngay khi trang vừa tải xong (Dự phòng)
document.addEventListener("DOMContentLoaded", () => {
    const resId = document.getElementById("qr-res-id").value;
    const tableName = document.getElementById("qr-table-name").value;
    if (tableName) {
        localStorage.setItem("currentTable", tableName);
    }

    if(resId) localStorage.setItem("currentRestaurant", resId);
});