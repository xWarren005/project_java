/* =========================================
   1. MOCK DATA (Giả lập dữ liệu Server)
   ========================================= */
const appData = {
    user: "Trần Văn Bếp",
    stats: {
        pending: 2,
        cooking: 3,
        ready: 1
    },
    activeOrders: [
        {
            id: 101,
            table: "Bàn 05",
            time: "10:30",
            statusLabel: "Đang nấu",
            statusCode: "cooking",
            items: [
                { name: "Bò Bít Tết", qty: 2 },
                { name: "Khoai tây chiên", qty: 1 }
            ]
        },
        {
            id: 102,
            table: "Bàn 12",
            time: "10:45",
            statusLabel: "Sẵn sàng",
            statusCode: "ready",
            items: [
                { name: "Mì Ý Carbonara", qty: 1 }
            ]
        }
    ],
    completedOrders: []
};

/* =========================================
   2. MAIN LOGIC & EVENTS
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    initApp();
    setupEventListeners();
});

function initApp() {
    // UPDATE: Thay đổi URL avatar sang màu 2E25D1
    const avatarUrl = "https://ui-avatars.com/api/?name=Chef+Master&background=2E25D1&color=fff";

    // Cập nhật DOM
    document.querySelector(".avatar").src = avatarUrl;
    document.getElementById("user-name-display").textContent = appData.user;

    renderStats();
    renderActiveOrders();
    renderCompletedOrders();
}

// ... Các phần code xử lý sự kiện (setupEventListeners, handleOrderAction) giữ nguyên như cũ ...

// Copy lại phần setupEventListeners và Business Logic ở phiên bản trước vào đây
// Hoặc nếu bạn muốn tôi viết lại đầy đủ cả file JS thì hãy báo nhé.
// Dưới đây là phần Render Functions đã được giữ nguyên logic:

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
    activeContainer.addEventListener("click", function(event) {
        const btn = event.target.closest(".btn-action");
        if (btn) {
            const orderId = parseInt(btn.getAttribute("data-id"));
            handleOrderAction(orderId);
        }
    });
}

function handleOrderAction(orderId) {
    const index = appData.activeOrders.findIndex(o => o.id === orderId);
    if (index !== -1) {
        const order = appData.activeOrders[index];
        if (order.statusCode === 'cooking') {
            order.statusCode = 'ready';
            order.statusLabel = 'Sẵn sàng';
            appData.stats.cooking--;
            appData.stats.ready++;
        } else if (order.statusCode === 'ready') {
            appData.stats.ready--;
            order.statusCode = 'served';
            order.statusLabel = 'Đã phục vụ';
            appData.activeOrders.splice(index, 1);
            appData.completedOrders.unshift(order);
        }
        renderStats();
        renderActiveOrders();
        renderCompletedOrders();
    }
}

function renderStats() {
    document.getElementById("stat-pending").textContent = appData.stats.pending;
    document.getElementById("stat-cooking").textContent = appData.stats.cooking;
    document.getElementById("stat-ready").textContent = appData.stats.ready;
}

function renderActiveOrders() {
    const container = document.getElementById("active-orders-container");
    container.innerHTML = "";

    if (appData.activeOrders.length === 0) {
        container.innerHTML = `<div class="empty-state">Không có đơn hàng nào</div>`;
        return;
    }

    appData.activeOrders.forEach(order => {
        const itemsHtml = order.items.map(item => `
            <div class="item-row">
                <span>${item.name}</span>
                <span class="qty">x${item.qty}</span>
            </div>
        `).join('');

        let btnText = "";
        let btnIcon = "";
        if(order.statusCode === 'cooking') {
            btnText = "Báo Sẵn Sàng";
            btnIcon = "fa-bell";
        } else if (order.statusCode === 'ready') {
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
                    <span class="status-badge ${order.statusCode}">${order.statusLabel}</span>
                </div>
                <div class="card-body">
                    ${itemsHtml}
                </div>
                <button class="btn-action" data-id="${order.id}">
                    <i class="fa-solid ${btnIcon}"></i> ${btnText}
                </button>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', cardHtml);
    });
}

function renderCompletedOrders() {
    const container = document.getElementById("completed-orders-container");
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