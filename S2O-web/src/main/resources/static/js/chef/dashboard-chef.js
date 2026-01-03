/* =========================================
   1. GLOBAL STATE (Dữ liệu ứng dụng)
   ========================================= */
let appData = {
    user: "Trần Văn Bếp", // Có thể lấy từ API profile sau này
    stats: {
        pending: 0,
        cooking: 0,
        ready: 0
    },
    activeOrders: [],
    completedOrders: []
};

/* =========================================
   2. MAIN LOGIC & EVENTS
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    initApp();
    setupEventListeners();

    // Tự động refresh dữ liệu mỗi 30 giây (Optional)
    setInterval(fetchDashboardData, 30000);
});

function initApp() {
    // 1. Setup Avatar
    const avatarUrl = "https://ui-avatars.com/api/?name=Chef+Master&background=2E25D1&color=fff";
    const avatarEl = document.querySelector(".avatar");
    if(avatarEl) avatarEl.src = avatarUrl;

    const userDisplay = document.getElementById("user-name-display");
    if(userDisplay) userDisplay.textContent = appData.user;

    // 2. Gọi API lấy dữ liệu thật
    fetchDashboardData();
}

/**
 * Hàm gọi API lấy dữ liệu từ Backend Java
 */
async function fetchDashboardData() {
    try {
        const response = await fetch('/api/chef/dashboard');
        if (!response.ok) throw new Error('Không thể tải dữ liệu');

        const data = await response.json();

        // Cập nhật State
        appData.stats = data.stats;

        // Map dữ liệu từ DTO Java sang cấu trúc JS cũ của bạn
        // Backend trả về: tableName, orderTime, status (PENDING, COOKING...)
        // Frontend cần: table, time, statusCode, statusLabel
        appData.activeOrders = data.activeOrders.map(order => {
            return {
                id: order.id,
                table: order.tableName,
                time: order.orderTime,
                statusCode: order.status, // Giá trị: "PENDING", "COOKING", "READY"
                statusLabel: getStatusLabel(order.status),
                items: order.items.map(item => ({
                    name: item.productName,
                    qty: item.quantity,
                    note: item.note
                }))
            };
        });

        // Render lại giao diện
        renderStats();
        renderActiveOrders();
        // renderCompletedOrders(); // Tạm thời chưa có API lấy đơn hoàn thành, để trống
    } catch (error) {
        console.error("Lỗi:", error);
    }
}

function getStatusLabel(status) {
    switch (status) {
        case 'PENDING': return 'Chờ xử lý';
        case 'COOKING': return 'Đang nấu';
        case 'READY': return 'Sẵn sàng';
        default: return status;
    }
}

function setupEventListeners() {
    const tabs = document.querySelectorAll(".tab-item");
    tabs.forEach(tab => {
        tab.addEventListener("click", function() {
            document.querySelectorAll(".tab-item").forEach(t => t.classList.remove("active"));
            document.querySelectorAll(".view-section").forEach(v => v.style.display = "none");
            this.classList.add("active");
            const targetId = this.getAttribute("data-target");
            const targetSection = document.getElementById(targetId);
            if(targetSection) targetSection.style.display = "block";
        });
    });

    const activeContainer = document.getElementById("active-orders-container");
    if(activeContainer) {
        activeContainer.addEventListener("click", function(event) {
            const btn = event.target.closest(".btn-action");
            if (btn) {
                const orderId = parseInt(btn.getAttribute("data-id"));
                // Lấy status hiện tại từ attribute data-status (được thêm vào lúc render)
                const currentStatus = btn.getAttribute("data-status");
                handleOrderAction(orderId, currentStatus);
            }
        });
    }
}

/**
 * Xử lý logic chuyển trạng thái và gọi API
 */
async function handleOrderAction(orderId, currentStatus) {
    let nextStatus = '';

    // Logic chuyển đổi trạng thái
    if (currentStatus === 'PENDING') {
        nextStatus = 'COOKING';
    } else if (currentStatus === 'COOKING') {
        nextStatus = 'READY';
    } else if (currentStatus === 'READY') {
        nextStatus = 'COMPLETED'; // Hoặc 'SERVED' tùy Enum Java
    }

    if (!nextStatus) return;

    // Hiển thị loading hoặc disable nút tạm thời (Optional)

    try {
        const response = await fetch(`/api/chef/order/${orderId}/status`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: nextStatus })
        });

        if (response.ok) {
            // Sau khi update thành công, tải lại dữ liệu mới nhất
            fetchDashboardData();

            // Nếu muốn giả lập chuyển sang tab hoàn thành:
            if (nextStatus === 'COMPLETED') {
                // Logic thêm vào mảng completedOrders cục bộ nếu cần
                // Nhưng tốt nhất là fetch lại API
            }
        } else {
            alert("Có lỗi xảy ra khi cập nhật trạng thái!");
        }
    } catch (error) {
        console.error("Error updating order:", error);
    }
}

/* =========================================
   3. RENDER FUNCTIONS (Giữ nguyên Layout HTML)
   ========================================= */

function renderStats() {
    // Kiểm tra null để tránh lỗi nếu HTML chưa load
    const statPending = document.getElementById("stat-pending");
    const statCooking = document.getElementById("stat-cooking");
    const statReady = document.getElementById("stat-ready");

    if(statPending) statPending.textContent = appData.stats.pending;
    if(statCooking) statCooking.textContent = appData.stats.cooking;
    if(statReady) statReady.textContent = appData.stats.ready;
}

function renderActiveOrders() {
    const container = document.getElementById("active-orders-container");
    if(!container) return;

    container.innerHTML = "";

    if (appData.activeOrders.length === 0) {
        container.innerHTML = `<div class="empty-state">Không có đơn hàng nào cần xử lý</div>`;
        return;
    }

    appData.activeOrders.forEach(order => {
        const itemsHtml = order.items.map(item => `
            <div class="item-row">
                <span>${item.name} ${item.note ? `<small class="text-muted">(${item.note})</small>` : ''}</span>
                <span class="qty">x${item.qty}</span>
            </div>
        `).join('');

        let btnText = "";
        let btnIcon = "";
        // Mapping class màu sắc dựa trên status code từ Backend
        let badgeClass = order.statusCode.toLowerCase();

        // Xác định nút bấm dựa trên trạng thái
        if(order.statusCode === 'PENDING') {
            btnText = "Bắt Đầu Nấu";
            btnIcon = "fa-fire";
        } else if(order.statusCode === 'COOKING') {
            btnText = "Báo Sẵn Sàng";
            btnIcon = "fa-bell";
        } else if (order.statusCode === 'READY') {
            btnText = "Hoàn Thành & Phục Vụ";
            btnIcon = "fa-check";
        }

        const cardHtml = `
            <div class="order-card">
                <div class="card-header">
                    <div>
                        <div class="table-name">${order.table} <span style="color:#999">#${order.id}</span></div>
                        <span class="order-time"><i class="far fa-clock"></i> ${order.time}</span>
                    </div>
                    <span class="status-badge ${badgeClass}">${order.statusLabel}</span>
                </div>
                <div class="card-body">
                    ${itemsHtml}
                </div>
                <button class="btn-action" data-id="${order.id}" data-status="${order.statusCode}">
                    <i class="fa-solid ${btnIcon}"></i> ${btnText}
                </button>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', cardHtml);
    });
}

function renderCompletedOrders() {
    const container = document.getElementById("completed-orders-container");
    if(!container) return;
    container.innerHTML = "";

    appData.completedOrders.forEach(order => {
        const itemsHtml = order.items.map(item => `
            <div class="item-row" style="background:#f5f6fa; color:#636e72">
                <span>${item.name}</span>
                <span class="qty" style="color:#636e72">x${item.qty}</span>
            </div>
        `).join('');

        const cardHtml = `
            <div class="order-card" style="opacity: 0.7">
                <div class="card-header">
                    <div class="table-name">${order.table}</div>
                    <span class="status-badge served">Đã xong</span>
                </div>
                <div class="card-body">
                    ${itemsHtml}
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', cardHtml);
    });
}