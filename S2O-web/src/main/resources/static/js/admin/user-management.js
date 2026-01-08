document.addEventListener('DOMContentLoaded', () => {
    console.log("User Management JS Loaded");

    // DOM Elements
    const tableBody = document.getElementById('userTableBody');
    const modal = document.getElementById('userModal');

    // Filters
    const searchInput = document.getElementById('searchInput');
    const roleFilter = document.getElementById('roleFilter');

    // Buttons
    const btnAdd = document.getElementById('btnAddUser');
    const btnClose = document.getElementById('btnCloseModal');
    const btnCancel = document.getElementById('btnCancelModal');
    const btnSave = document.getElementById('btnSaveUser');

    // Form Inputs
    const inpUsername = document.getElementById('uUsername');
    const inpPassword = document.getElementById('uPassword');
    const inpFullName = document.getElementById('uFullName');
    const inpEmail = document.getElementById('uEmail');
    const selRole = document.getElementById('uRole');
    const modalTitle = document.getElementById('modalUserTitle');

    let allUsers = [];

    // ===============================================
    // 1. SETUP EVENT LISTENERS (Gán sự kiện an toàn)
    // ===============================================

    if (btnAdd) btnAdd.addEventListener('click', openCreateUserModal);
    if (btnClose) btnClose.addEventListener('click', closeUserModal);
    if (btnCancel) btnCancel.addEventListener('click', closeUserModal);

    // Sự kiện tìm kiếm & lọc
    if (searchInput) searchInput.addEventListener('input', () => renderTable(allUsers));
    if (roleFilter) roleFilter.addEventListener('change', () => renderTable(allUsers));

    // ===============================================
    // 2. LOGIC API
    // ===============================================

    async function loadUsers() {
        try {
            const res = await fetch('/api/admin/users');
            if (res.ok) {
                const data = await res.json();
                allUsers = data.list;

                // Update thống kê
                if(document.getElementById('user-total')) {
                    document.getElementById('user-total').innerText = data.total;
                }
                if(searchInput) {
                    searchInput.value = "";
                }
                renderTable(allUsers);
            } else {
                console.error("Lỗi tải dữ liệu user");
            }
        } catch (e) { console.error(e); }
    }

    // ===============================================
    // 3. RENDER TABLE (Có tìm kiếm)
    // ===============================================
    function renderTable(data) {
        // 1. Lọc dữ liệu
        const keyword = searchInput ? searchInput.value.toLowerCase() : "";
        const role = roleFilter ? roleFilter.value : "";

        const filtered = data.filter(u => {
            const matchKeyword = (u.username || "").toLowerCase().includes(keyword) ||
                (u.fullName || "").toLowerCase().includes(keyword) ||
                (u.email || "").toLowerCase().includes(keyword);

            // So sánh Role (Lưu ý: DB thường trả về UPPERCASE)
            const matchRole = role === "" || (u.role && u.role.toUpperCase() === role.toUpperCase());

            return matchKeyword && matchRole;
        });

        // 2. Vẽ bảng
        tableBody.innerHTML = '';
        if (filtered.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 20px;">Không tìm thấy kết quả</td></tr>`;
            return;
        }

        filtered.forEach(user => {
            const row = document.createElement('tr');

            // Xử lý Role Badge màu mè chút
            let roleBadge = `<span class="badge badge-active">${user.role}</span>`;

            switch (user.role) {
                case 'ADMIN':
                    roleBadge = `<span class="badge" style="background:#ede9fe; color:#7c3aed">ADMIN</span>`; // Tím
                    break;
                case 'MANAGER':
                    roleBadge = `<span class="badge" style="background:#dbeafe; color:#2563eb">MANAGER</span>`; // Xanh dương
                    break;
                case 'CHEF':
                    roleBadge = `<span class="badge" style="background:#ffedd5; color:#c2410c">CHEF</span>`; // Cam
                    break;
                case 'CASHIER':
                    roleBadge = `<span class="badge" style="background:#ecfccb; color:#4d7c0f">CASHIER</span>`; // Xanh lá mạ
                    break;
                case 'CUSTOMER':
                    roleBadge = `<span class="badge" style="background:#f3f4f6; color:#374151">CUSTOMER</span>`; // Xám
                    break;
            }
            // Nút sửa xóa - Dùng ID để gọi hàm global bên dưới
            const btns = `
                <button class="btn-action btn-edit" onclick="openEditUserModal(${user.id})"><i data-lucide="pencil"></i></button>
                <button class="btn-action btn-delete" onclick="handleDeleteUser(${user.id})"><i data-lucide="trash-2"></i></button>
            `;

            row.innerHTML = `
                <td class="fw-bold">${user.fullName}</td>
                <td>${user.email || '-'}</td>
                <td>${roleBadge}</td>
                <td><span style="color:green; font-size:12px">● Hoạt động</span></td>
                <td><div class="action-buttons">${btns}</div></td>
            `;
            tableBody.appendChild(row);
        });
        if(window.lucide) lucide.createIcons();
    }

    // ===============================================
    // 4. MODAL & ACTIONS
    // ===============================================

    function openCreateUserModal() {
        if(searchInput) {
            searchInput.value = "";
            renderTable(allUsers); // Render lại bảng full
        }
        // Reset form
        inpUsername.value = ""; inpUsername.disabled = false;
        inpPassword.value = "";
        inpFullName.value = "";
        inpEmail.value = "";
        selRole.value = "USER"; // Mặc định

        modalTitle.innerText = "Thêm người dùng";
        btnSave.onclick = executeCreate; // Gán hàm Create
        modal.classList.add('show');
    }

    // Gán vào window để HTML onclick gọi được
    window.openEditUserModal = (id) => {
        if(searchInput) {
            searchInput.value = "";
            renderTable(allUsers);
        }
        const user = allUsers.find(u => u.id === id);
        if(!user) return alert("Không tìm thấy user!");

        inpUsername.value = user.username; inpUsername.disabled = true; // Cấm sửa username
        inpPassword.value = ""; // Reset mật khẩu
        inpFullName.value = user.fullName;
        inpEmail.value = user.email;
        selRole.value = user.role;

        modalTitle.innerText = "Cập nhật thông tin";

        // Gán hàm Update (dùng closure giữ ID)
        btnSave.onclick = function() { executeUpdate(id); };

        modal.classList.add('show');
    };

    function closeUserModal() {
        modal.classList.remove('show');
    }

    // --- API CALLS ---

    async function executeCreate() {
        const payload = {
            username: inpUsername.value.trim(),
            password: inpPassword.value,
            fullName: inpFullName.value.trim(),
            email: inpEmail.value.trim(),
            role: selRole.value
        };

        if(!payload.username || !payload.password) return alert("Vui lòng nhập Username và Password");

        try {
            const res = await fetch('/api/admin/users', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });

            if(res.ok) {
                alert("Thêm thành công!");
                closeUserModal();
                loadUsers();
            } else {
                const text = await res.text();
                alert("Lỗi: " + text);
            }
        } catch(e) { alert("Lỗi kết nối server"); }
    }

    async function executeUpdate(id) {
        const payload = {
            fullName: inpFullName.value.trim(),
            email: inpEmail.value.trim(),
            password: inpPassword.value, // Nếu rỗng server sẽ bỏ qua
            role: selRole.value
        };

        try {
            const res = await fetch(`/api/admin/users/${id}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });

            if(res.ok) {
                alert("Cập nhật thành công!");
                closeUserModal();
                loadUsers();
            } else { alert("Lỗi cập nhật"); }
        } catch(e) { alert("Lỗi kết nối server"); }
    }

    window.handleDeleteUser = async (id) => {
        if(!confirm("Bạn chắc chắn muốn xóa user này?")) return;
        try {
            const res = await fetch(`/api/admin/users/${id}`, { method: 'DELETE' });
            if(res.ok) loadUsers();
            else alert("Lỗi khi xóa");
        } catch(e) { alert("Lỗi kết nối"); }
    }

    // Start
    loadUsers();
});