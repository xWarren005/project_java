// Biến toàn cục lưu dữ liệu bàn
let tablesData = [];

document.addEventListener("DOMContentLoaded", () => {

    //Menu cashier

    renderTopHeader();
    renderCashierMenu('tables');

    fetchTableData();
    // Tự động refresh sau mỗi 30 giây để cập nhật trạng thái bàn
    setInterval(fetchTableData, 30000);
});

// Hàm gọi API lấy dữ liệu
async function fetchTableData() {
    try {
        const response = await fetch('/api/cashier/tables');
        if (!response.ok) throw new Error('Lỗi kết nối server');

        tablesData = await response.json();

        renderGrid();
        updateStats();
    } catch (error) {
        console.error("Không thể tải dữ liệu bàn:", error);
    }
}

// 1. Render Lưới Bàn
function renderGrid() {
    const grid = document.getElementById("table-grid");
    if (!tablesData || tablesData.length === 0) {
        grid.innerHTML = "<p style='text-align:center; width:100%'>Đang tải dữ liệu hoặc chưa có bàn nào...</p>";
        return;
    }

    grid.innerHTML = tablesData.map(t => {
        let statusClass = "";
        let statusText = "";
        let extraHtml = "";

        // Mapping trạng thái từ API (empty, busy, reserved) sang giao diện
        if (t.status === 'empty') {
            statusClass = "empty";
            statusText = "Trống";
        } else if (t.status === 'busy') {
            statusClass = "busy";
            statusText = "Có Khách";
            extraHtml = `<div class="time-counter"><i class="fa-regular fa-clock"></i> ${t.time}</div>`;
        } else if (t.status === 'reserved') {
            statusClass = "reserved";
            statusText = "Đã Đặt";
        }

        return `
            <div class="table-item ${statusClass}" onclick="handleTableClick(${t.id})">
                <div class="t-name">${t.name}</div>
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
    if (!table) return;

    if (table.status === 'busy') {
        openModal(table);
    } else {
        // Có thể mở rộng logic: Click bàn trống thì mở trang tạo đơn
        // window.location.href = `/cashier/create-order?tableId=${id}`;
        alert("Bàn này đang trống. Bạn có thể tạo đơn mới.");
    }
}

// 4. Mở Modal Chi Tiết
function openModal(table) {
    const modal = document.getElementById("table-modal");

    document.getElementById("modal-title").innerText = table.name;
    document.getElementById("modal-capacity").innerText = `Sức chứa: ${table.capacity} người`;

    // Format tiền tệ Việt Nam
    const formattedTotal = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(table.total);
    document.getElementById("modal-total").innerText = formattedTotal;

    document.getElementById("modal-time").innerText = `Bắt đầu: ${table.time}`;

    // Fill danh sách món
    if (table.orders && table.orders.length > 0) {
        const listHtml = table.orders.map(item => `
            <li>
                <span>${item.name} <span style="font-weight:bold">x${item.qty}</span></span>
                <span></span>
            </li>
        `).join('');
        document.getElementById("modal-items").innerHTML = listHtml;
    } else {
        document.getElementById("modal-items").innerHTML = "<li style='font-style:italic'>Chưa có món nào</li>";
    }

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