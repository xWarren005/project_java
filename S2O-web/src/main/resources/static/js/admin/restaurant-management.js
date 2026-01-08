/* File: src/main/resources/static/js/admin/restaurant-management.js */

document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const tableBody = document.getElementById('restaurantTableBody');
    const modal = document.getElementById('addModal');
    const inputName = document.getElementById('resName');
    const inputAddress = document.getElementById('resAddress');
    const modalTitle = document.getElementById('modalTitle');
    const btnSave = document.getElementById('btnSave');

    // Biến lưu danh sách (để tìm dữ liệu khi sửa)
    let allRestaurants = [];

    // ===============================================
    // 1. CÁC HÀM GỌI API (LOGIC MẠNG)
    // ===============================================

    // Load danh sách
    async function loadRestaurants() {
        try {
            const res = await fetch('/api/admin/restaurants');
            if (res.ok) {
                const data = await res.json();
                allRestaurants = data.list; // Lưu lại để dùng cho Edit

                // Update stats
                document.getElementById('res-total').innerText = data.total;
                document.getElementById('res-pending').innerText = data.pending;
                document.getElementById('res-rating').innerText = data.rating;

                renderTable(allRestaurants);
            }
        } catch (e) { console.error(e); }
    }

    // A. Hàm thực thi THÊM MỚI (POST)
    async function executeCreate() {
        const name = inputName.value.trim();
        const address = inputAddress.value.trim();
        if(!name || !address) return alert("Vui lòng nhập đủ thông tin");

        try {
            const res = await fetch('/api/admin/restaurants', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, address })
            });

            if(res.ok) {
                alert("Thêm mới thành công!");
                closeAddModal();
                loadRestaurants();
            } else {
                alert("Lỗi khi thêm mới");
            }
        } catch(e) { alert("Lỗi server"); }
    }

    // B. Hàm thực thi CẬP NHẬT (PUT)
    async function executeUpdate(id) {
        const name = inputName.value.trim();
        const address = inputAddress.value.trim();
        if(!name || !address) return alert("Vui lòng nhập đủ thông tin");

        try {
            const res = await fetch(`/api/admin/restaurants/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, address })
            });

            if(res.ok) {
                alert("Cập nhật thành công!");
                closeAddModal();
                loadRestaurants();
            } else {
                alert("Lỗi khi cập nhật");
            }
        } catch(e) { alert("Lỗi server"); }
    }

    // ===============================================
    // 2. CÁC HÀM GIAO DIỆN (UI)
    // ===============================================

    // Mở Modal chế độ THÊM MỚI
    window.openCreateModal = () => {
        // 1. Reset Form
        inputName.value = "";
        inputAddress.value = "";

        // 2. Đổi tiêu đề
        modalTitle.innerText = "Thêm nhà hàng mới";

        // 3. Gắn hàm executeCreate vào nút Lưu
        btnSave.onclick = executeCreate;

        // 4. Hiện Modal
        modal.classList.add('show');
    };

    // Mở Modal chế độ CHỈNH SỬA
    window.openEditModal = (id) => {
        // 1. Tìm dữ liệu cũ từ mảng allRestaurants
        const item = allRestaurants.find(r => r.id === id);
        if(!item) return;

        // 2. Điền dữ liệu cũ vào Form
        inputName.value = item.name;
        inputAddress.value = item.address;

        // 3. Đổi tiêu đề
        modalTitle.innerText = `Cập nhật: ${item.name}`;

        // 4. Gắn hàm executeUpdate vào nút Lưu (dùng closure để truyền ID)
        btnSave.onclick = function() {
            executeUpdate(id);
        };

        // 5. Hiện Modal
        modal.classList.add('show');
    };

    window.closeAddModal = () => {
        modal.classList.remove('show');
    };

    // Render Bảng
    function renderTable(data) {
        tableBody.innerHTML = '';
        data.forEach(item => {
            const row = document.createElement('tr');

            // Nút Sửa gọi openEditModal, Nút Xóa gọi handleDelete
            let btns = `
                <button class="btn-action btn-edit" onclick="openEditModal(${item.id})"><i data-lucide="pencil"></i></button>
                <button class="btn-action btn-delete" onclick="handleDelete(${item.id})"><i data-lucide="trash-2"></i></button>
            `;

            row.innerHTML = `
                <td class="fw-bold">${item.name}</td>
                <td>${item.address}</td>
                <td>${getStatusBadge(item.status, item.id)}</td>
                <td>${item.rating || 'N/A'} <i data-lucide="star" style="width:12px"></i></td>
                <td><div class="action-buttons">${btns}</div></td>
            `;
            tableBody.appendChild(row);
        });
        if(window.lucide) lucide.createIcons();
    }

    // Helper Badge (Giữ nguyên)
    const getStatusBadge = (status, id) => {
        if (status === 'PENDING') return `<button class="badge-approve-btn" onclick="handleApprove(${id})">Chờ duyệt</button>`;
        return status === 'ACTIVE' || status === 'APPROVED' ? '<span class="badge badge-active">Hoạt động</span>' : '<span class="badge badge-inactive">Ngừng</span>';
    };

    // Các hàm Approve/Delete giữ nguyên...
    window.handleApprove = async (id) => {
        if(confirm('Duyệt nhà hàng này?')) {
            await fetch(`/api/admin/restaurants/${id}/approve`, { method: 'PUT' });
            loadRestaurants();
        }
    }
    window.handleDelete = async (id) => {
        if(confirm('Xóa nhà hàng này?')) {
            await fetch(`/api/admin/restaurants/${id}`, { method: 'DELETE' });
            loadRestaurants();
        }
    }

    // Chạy lần đầu
    loadRestaurants();
});