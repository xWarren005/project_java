// --- DATA GIẢ LẬP ---
// status: 0 (Trống), 1 (Có Khách), 2 (Đã Đặt)
let tablesData = [
    { id: 1, name: "Bàn #1", seats: 2, status: 0 },
    { id: 2, name: "Bàn #2", seats: 4, status: 1 },
    { id: 3, name: "Bàn #3", seats: 6, status: 2 },
    { id: 4, name: "Bàn #4", seats: 2, status: 0 },
    { id: 5, name: "Bàn #5", seats: 4, status: 1 },
    { id: 6, name: "Bàn #6", seats: 6, status: 2 },
    { id: 7, name: "Bàn #7", seats: 2, status: 0 },
    { id: 8, name: "Bàn #8", seats: 4, status: 1 }
];

// --- INIT ---
document.addEventListener("DOMContentLoaded", () => {
    renderTables();

    // Sự kiện nút thêm bàn
    document.getElementById("btn-add-table").addEventListener("click", () => {
        document.getElementById("modal-overlay").classList.remove("hidden");
    });

    // Sự kiện submit form thêm bàn
    document.getElementById("table-form").addEventListener("submit", handleAddTable);
});

// --- RENDER ---
function renderTables() {
    const container = document.getElementById("tables-container");

    container.innerHTML = tablesData.map(t => {
        // Xác định class màu và text hiển thị dựa trên status
        let badgeClass = "";
        let statusText = "";

        if (t.status === 0) { badgeClass = "badge-free"; statusText = "Trống"; }
        else if (t.status === 1) { badgeClass = "badge-busy"; statusText = "Có Khách"; }
        else { badgeClass = "badge-reserved"; statusText = "Đã Đặt"; }

        return `
            <div class="table-card">
                <div class="card-header">
                    <div>
                        <h4 class="table-name">${t.name}</h4>
                        <span class="table-seats"><i class="fa-solid fa-user-group"></i> ${t.seats} chỗ ngồi</span>
                    </div>
                    <span class="status-badge ${badgeClass}">${statusText}</span>
                </div>

                <div class="status-select-group">
                    <label class="status-label">Trạng thái</label>
                    <select class="status-select" onchange="updateStatus(${t.id}, this.value)">
                        <option value="0" ${t.status == 0 ? "selected" : ""}>Trống</option>
                        <option value="1" ${t.status == 1 ? "selected" : ""}>Có Khách</option>
                        <option value="2" ${t.status == 2 ? "selected" : ""}>Đã Đặt</option>
                    </select>
                </div>

                <button class="btn-qr" onclick="showQR('${t.name}')">
                    <i class="fa-solid fa-qrcode"></i> Xem Mã QR
                </button>
            </div>
        `;
    }).join('');
}

// --- LOGIC XỬ LÝ ---

// 1. Cập nhật trạng thái khi chọn Select box
function updateStatus(id, newStatus) {
    const tableIndex = tablesData.findIndex(t => t.id === id);
    if (tableIndex !== -1) {
        tablesData[tableIndex].status = parseInt(newStatus);
        renderTables(); // Render lại để cập nhật màu Badge
    }
}

// 2. Thêm bàn mới
function handleAddTable(e) {
    e.preventDefault();
    const name = document.getElementById("inp-name").value;
    const seats = document.getElementById("inp-seats").value;

    if (name && seats) {
        const newTable = {
            id: Date.now(), // Tạo ID ngẫu nhiên
            name: name,
            seats: parseInt(seats),
            status: 0 // Mặc định là trống
        };

        tablesData.push(newTable);
        renderTables();
        closeModal();
        e.target.reset(); // Reset form
        alert("Thêm bàn mới thành công!");
    }
}

// 3. Đóng modal
function closeModal() {
    document.getElementById("modal-overlay").classList.add("hidden");
}

// 4. Xem QR (Giả lập)
function showQR(tableName) {
    alert(`Đang hiển thị mã QR cho ${tableName}\n(Chức năng in/tải QR sẽ được tích hợp sau)`);
}