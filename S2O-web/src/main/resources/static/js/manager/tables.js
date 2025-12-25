// Biến toàn cục
let tablesData = [];
const API_TABLES_URL = '/api/manager/tables';

// --- INIT ---
document.addEventListener("DOMContentLoaded", () => {
    fetchTables();

    // Sự kiện nút mở modal thêm bàn
    const btnAdd = document.getElementById("btn-add-table");
    if(btnAdd) {
        btnAdd.addEventListener("click", () => {
            document.getElementById("modal-overlay").classList.remove("hidden");
        });
    }

    // Sự kiện submit form thêm bàn
    const formTable = document.getElementById("table-form");
    if(formTable) {
        formTable.addEventListener("submit", handleAddTable);
    }
});

// --- 1. LẤY DỮ LIỆU TỪ SERVER (GET) ---
function fetchTables() {
    fetch(API_TABLES_URL)
        .then(response => {
            if (!response.ok) throw new Error("Không thể tải dữ liệu");
            return response.json();
        })
        .then(data => {
            tablesData = data;
            renderTables();
        })
        .catch(err => console.error("Lỗi tải bàn:", err));
}

// --- RENDER GIAO DIỆN ---
function renderTables() {
    const container = document.getElementById("tables-container");

    if (!container) return; // Guard clause nếu không tìm thấy div

    container.innerHTML = tablesData.map(t => {
        let badgeClass = "";
        let statusText = "";

        // Logic màu sắc trạng thái
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

                <div class="qr-section" style="text-align: center; margin-top: 15px; border-top: 1px dashed #eee; padding-top: 10px;">
                    <span style="font-size: 12px; color: #666; display: block; margin-bottom: 5px;">Quét để gọi món:</span>
                    
                    <img src="${API_TABLES_URL}/${t.id}/qr" 
                         alt="QR Bàn ${t.name}" 
                         title="Click chuột phải để tải ảnh về"
                         style="width: 140px; height: 140px; object-fit: contain; border: 1px solid #e0e0e0; border-radius: 8px; padding: 5px; background: white;">
                         
                    <div style="margin-top: 5px;">
                        <a href="${API_TABLES_URL}/${t.id}/qr" download="QR_${t.name}.png" class="btn-link" style="font-size: 13px;">
                            <i class="fa-solid fa-download"></i> Tải ảnh
                        </a>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// --- LOGIC XỬ LÝ ---

// 2. Cập nhật trạng thái (PUT)
function updateStatus(id, newStatus) {
    // Gọi API: /api/manager/tables/{id}/status?status={newStatus}
    fetch(`${API_TABLES_URL}/${id}/status?status=${newStatus}`, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                // Cập nhật local data để không cần load lại trang (giúp UI mượt hơn)
                const table = tablesData.find(t => t.id === id);
                if (table) table.status = parseInt(newStatus);
                renderTables(); // Vẽ lại giao diện để cập nhật màu Badge
            } else {
                alert("Lỗi cập nhật trạng thái! Vui lòng thử lại.");
            }
        })
        .catch(err => console.error("Lỗi update:", err));
}

// 3. Thêm bàn mới (POST)
function handleAddTable(e) {
    e.preventDefault();
    const nameInput = document.getElementById("inp-name");
    const seatsInput = document.getElementById("inp-seats");

    const payload = {
        name: nameInput.value,
        seats: parseInt(seatsInput.value),
        status: 0
    };

    fetch(API_TABLES_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) throw new Error("Lỗi khi thêm bàn");
            return response.json();
        })
        .then(newTable => {
            // Server trả về đối tượng bàn mới (đã bao gồm ID và Link QR ngầm)
            tablesData.push(newTable);
            renderTables();
            closeModal();
            e.target.reset(); // Xóa trắng form
            // alert("Thêm bàn mới thành công!"); // Có thể bỏ alert cho đỡ phiền
        })
        .catch(err => {
            console.error("Lỗi thêm bàn:", err);
            alert("Có lỗi xảy ra khi thêm bàn.");
        });
}

// 4. Các hàm tiện ích
function closeModal() {
    const modal = document.getElementById("modal-overlay");
    if(modal) modal.classList.add("hidden");
}

// Đóng modal khi click ra ngoài vùng trắng
window.onclick = function(event) {
    const modal = document.getElementById("modal-overlay");
    if (event.target == modal) {
        closeModal();
    }
}