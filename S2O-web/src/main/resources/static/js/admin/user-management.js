document.addEventListener('DOMContentLoaded', () => {
    // 1. DATA MẪU
    const users = [
        { id: 1, name: "Nguyễn Văn A", email: "admin@system.com", role: "Admin", status: "active" },
        { id: 2, name: "Trần Thị B", email: "manager@pho24.com", role: "Restaurant Manager", status: "active" },
        { id: 3, name: "Lê Văn C", email: "user@email.com", role: "Customer", status: "active" },
        { id: 4, name: "Phạm Thị D", email: "manager@sushi.com", role: "Restaurant Manager", status: "active" },
        { id: 5, name: "Hoàng Văn E", email: "user2@email.com", role: "Customer", status: "inactive" }
    ];

    const tableBody = document.getElementById('userTableBody');
    const searchInput = document.getElementById('searchInput');
    const roleFilter = document.getElementById('roleFilter');

    // 2. RENDER TABLE
    const renderTable = () => {
        const keyword = searchInput.value.toLowerCase().trim();
        const selectedRole = roleFilter.value;

        // Logic Lọc: Kết hợp Tìm kiếm + Dropdown
        const filteredUsers = users.filter(user => {
            const matchKeyword = user.name.toLowerCase().includes(keyword) ||
                                 user.email.toLowerCase().includes(keyword);
            const matchRole = selectedRole === "" || user.role === selectedRole;
            return matchKeyword && matchRole;
        });

        tableBody.innerHTML = '';

        if (filteredUsers.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 40px; color: #6b7280;">Không tìm thấy kết quả phù hợp</td></tr>`;
            return;
        }

        filteredUsers.forEach(user => {
            const row = document.createElement('tr');

            // Render Badge Trạng thái
            const statusHtml = user.status === 'active'
                ? '<span class="badge badge-active">Hoạt động</span>'
                : '<span class="badge badge-inactive">Ngừng hoạt động</span>';

            // Xử lý hiển thị Tên vai trò (Việt hóa) & Màu sắc
            let roleDisplay = user.role;
            let roleStyle = 'color: #4b5563'; // Mặc định xám

            if (user.role === 'Admin') {
                roleStyle = 'color: #7c3aed; font-weight: 500'; // Tím
            } else if (user.role === 'Restaurant Manager') {
                roleDisplay = 'Quản lý';
                roleStyle = 'color: #2563eb; font-weight: 500'; // Xanh dương
            } else if (user.role === 'Customer') {
                roleDisplay = 'Khách hàng';
            }

            row.innerHTML = `
                <td class="fw-bold">${user.name}</td>
                <td>${user.email}</td>
                <td><span style="${roleStyle}">${roleDisplay}</span></td>
                <td>${statusHtml}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-edit" onclick="editUser(${user.id})" title="Chỉnh sửa">
                            <i data-lucide="pencil"></i>
                        </button>
                    </div>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Tạo lại icon Lucide sau khi render HTML
        if(window.lucide) lucide.createIcons();
    };

    // Hàm giả lập chức năng sửa
    window.editUser = (id) => {
        alert("Đang mở form sửa cho User ID: " + id);
    };

    // 3. EVENTS
    searchInput.addEventListener('input', renderTable);
    roleFilter.addEventListener('change', renderTable);

    // Chạy lần đầu
    renderTable();
});