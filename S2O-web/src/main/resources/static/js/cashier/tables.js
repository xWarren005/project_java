// Dữ liệu mô phỏng 12 bàn
const tablesData = [
    { id: 1, name: "Bàn #1", capacity: 2, status: "empty" },
    { id: 2, name: "Bàn #2", capacity: 4, status: "busy", time: "16 phút trước", total: 165000,
        orders: [
            { name: "Phở Bò Đặc Biệt", qty: 2 },
            { name: "Gỏi Cuốn Tôm Thịt", qty: 1 }
        ]
    },
    { id: 3, name: "Bàn #3", capacity: 6, status: "reserved" },
    { id: 4, name: "Bàn #4", capacity: 2, status: "empty" },
    { id: 5, name: "Bàn #5", capacity: 4, status: "busy", time: "11 phút trước", total: 220000,
        orders: [{ name: "Lẩu Thái", qty: 1 }, { name: "Pepsi", qty: 4 }]
    },
    { id: 6, name: "Bàn #6", capacity: 6, status: "reserved" },
    { id: 7, name: "Bàn #7", capacity: 2, status: "empty" },
    { id: 8, name: "Bàn #8", capacity: 4, status: "busy", time: "30 phút trước", total: 450000,
        orders: [{ name: "Combo Nướng", qty: 1 }]
    },
    { id: 9, name: "Bàn #9", capacity: 6, status: "reserved" },
    { id: 10, name: "Bàn #10", capacity: 2, status: "empty" },
    { id: 11, name: "Bàn #11", capacity: 4, status: "busy", time: "5 phút trước", total: 80000,
        orders: [{ name: "Cafe Sữa", qty: 2 }]
    },
    { id: 12, name: "Bàn #12", capacity: 6, status: "reserved" },
];

document.addEventListener("DOMContentLoaded", () => {
    renderGrid();
    updateStats();
});

// 1. Render Lưới Bàn
function renderGrid() {
    const grid = document.getElementById("table-grid");
    grid.innerHTML = tablesData.map(t => {
        // Xác định class và text hiển thị
        let statusClass = "";
        let statusText = "";
        let extraHtml = "";

        if (t.status === 'empty') {
            statusClass = "empty";
            statusText = "Trống";
        } else if (t.status === 'busy') {
            statusClass = "busy";
            statusText = "Có Khách";
            // Bàn có khách thì hiện thêm thời gian
            extraHtml = `<div class="time-counter"><i class="fa-regular fa-clock"></i> ${t.time}</div>`;
        } else {
            statusClass = "reserved";
            statusText = "Đã Đặt";
        }

        return `
            <div class="table-item ${statusClass}" onclick="handleTableClick(${t.id})">
                <div class="t-name">#${t.id}</div>
                <div class="t-capacity">
                    <i class="fa-solid fa-user-group"></i> ${t.capacity} chỗ
                </div>
                <span class="t-badge">${statusText}</span>
                ${extraHtml}
            </div>
        `;
    }).join('');
}

// 2. Cập nhật số liệu thống kê
function updateStats() {
    document.getElementById("stat-empty").innerText = tablesData.filter(t => t.status === 'empty').length;
    document.getElementById("stat-busy").innerText = tablesData.filter(t => t.status === 'busy').length;
    document.getElementById("stat-reserved").innerText = tablesData.filter(t => t.status === 'reserved').length;
    document.getElementById("stat-total").innerText = tablesData.length;
}

// 3. Xử lý khi click vào bàn
function handleTableClick(id) {
    const table = tablesData.find(t => t.id === id);

    // Nếu bàn Đang có khách (busy) thì mở Modal chi tiết
    if (table.status === 'busy') {
        openModal(table);
    } else {
        // Demo: Bàn trống click vào cũng hiện alert
        // Thực tế sẽ mở form đặt bàn
        console.log("Bàn này chưa có khách");
    }
}

// 4. Mở Modal
function openModal(table) {
    const modal = document.getElementById("table-modal");

    // Fill thông tin
    document.getElementById("modal-title").innerText = table.name;
    document.getElementById("modal-capacity").innerText = `Sức chứa: ${table.capacity} người`;
    document.getElementById("modal-total").innerText = table.total.toLocaleString('vi-VN') + "đ";
    document.getElementById("modal-time").innerText = `Đặt hàng ${table.time}`;

    // Fill danh sách món
    const listHtml = table.orders.map(item => `
        <li>
            <span>${item.name} x${item.qty}</span>
            <span></span>
        </li>
    `).join('');
    document.getElementById("modal-items").innerHTML = listHtml;

    // Hiển thị modal
    modal.style.display = "flex";
}

// 5. Đóng Modal
function closeModal() {
    document.getElementById("table-modal").style.display = "none";
}

// Click ra ngoài thì đóng
window.onclick = function(e) {
    const modal = document.getElementById("table-modal");
    if (e.target === modal) {
        closeModal();
    }
}