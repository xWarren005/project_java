document.addEventListener('DOMContentLoaded', () => {
    // 1. MOCK DATA (Dữ liệu giả lập ban đầu)
    let restaurants = [
        {
            id: 1,
            name: "Phở 24",
            address: "123 Lê Lợi, Q1, TP.HCM",
            status: "active", // active, pending, inactive
            rating: 4.5
        },
        {
            id: 2,
            name: "Sushi World",
            address: "456 Nguyễn Huệ, Q3, TP.HCM",
            status: "active",
            rating: 4.8
        },
        {
            id: 3,
            name: "BBQ House",
            address: "789 Võ Văn Tần, Q2, TP.HCM",
            status: "pending",
            rating: null // Chưa có đánh giá
        },
        {
            id: 4,
            name: "Vegan Garden",
            address: "321 Pasteur, Q7, TP.HCM",
            status: "active",
            rating: 4.3
        },
        {
            id: 5,
            name: "Pizza Express",
            address: "654 Hai Bà Trưng, Q1, TP.HCM",
            status: "inactive",
            rating: 4.1
        }
    ];

    // 2. DOM ELEMENTS
    const tableBody = document.querySelector('.data-table tbody');
    const searchInput = document.querySelector('.search-input');

    // Stats Elements
    const totalCountEl = document.querySelector('.stat-value:nth-of-type(1)'); // Hoặc gán ID trong HTML để select chính xác hơn
    const pendingCountEl = document.querySelectorAll('.stat-value')[1];
    const avgRatingEl = document.querySelectorAll('.stat-value')[2];

    // 3. HELPER FUNCTIONS

    // Format trạng thái thành HTML badge
    const getStatusBadge = (status) => {
        switch(status) {
            case 'active':
                return '<span class="badge badge-active">Hoạt động</span>';
            case 'pending':
                return '<span class="badge badge-pending">Chờ duyệt</span>';
            case 'inactive':
                return '<span class="badge badge-inactive">Ngừng hoạt động</span>';
            default:
                return '<span class="badge badge-inactive">Không xác định</span>';
        }
    };

    // Format điểm đánh giá
    const getRatingHtml = (rating) => {
        if (!rating) return '<span class="text-gray-400">N/A</span>';
        return `<span class="rating">${rating} <i data-lucide="star" class="icon-star"></i></span>`;
    };

    // Cập nhật số liệu thống kê (Dashboard Stats)
    const updateStats = () => {
        const total = restaurants.length;
        const pending = restaurants.filter(r => r.status === 'pending').length;

        // Tính trung bình đánh giá (chỉ tính những quán có rating)
        const ratedRestaurants = restaurants.filter(r => r.rating !== null && r.rating !== undefined);
        const totalRating = ratedRestaurants.reduce((sum, r) => sum + r.rating, 0);
        const avg = ratedRestaurants.length ? (totalRating / ratedRestaurants.length).toFixed(1) : 0;
        const statValues = document.querySelectorAll('.stat-value');
        if(statValues.length >= 3) {
            statValues[0].textContent = total;
            statValues[1].textContent = pending;
            statValues[2].textContent = avg;
        }
    };

    // 4. RENDER FUNCTION
    const renderTable = (data) => {
        tableBody.innerHTML = ''; // Xóa nội dung cũ

        if (data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 20px;">Không tìm thấy kết quả</td></tr>`;
            return;
        }

        data.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="fw-bold">${item.name}</td>
                <td>${item.address}</td>
                <td>${getStatusBadge(item.status)}</td>
                <td>${getRatingHtml(item.rating)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-action btn-edit" onclick="handleEdit(${item.id})">
                            <i data-lucide="pencil"></i>
                        </button>
                        ${item.status === 'pending' ? `
                        <button class="btn-action btn-check" onclick="handleApprove(${item.id})" title="Phê duyệt">
                            <i data-lucide="check"></i>
                        </button>` : ''}
                        <button class="btn-action btn-delete" onclick="handleDelete(${item.id})">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                </td>
            `;
            tableBody.appendChild(row);
        });

        // Re-init Icons sau khi render HTML mới
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
        }
    };

    // 5. EVENT LISTENERS & HANDLERS

    // Tìm kiếm
    searchInput.addEventListener('input', (e) => {
        const keyword = e.target.value.toLowerCase();
        const filteredData = restaurants.filter(item =>
            item.name.toLowerCase().includes(keyword) ||
            item.address.toLowerCase().includes(keyword)
        );
        renderTable(filteredData);
    });

    // Các hàm xử lý hành động (được gọi từ onclick trong HTML string)
    // Cần gán vào window object để truy cập được từ chuỗi HTML
    window.handleEdit = (id) => {
        const item = restaurants.find(r => r.id === id);
        alert(`Chức năng chỉnh sửa cho nhà hàng: ${item.name}\n(Đang phát triển)`);
    };

    window.handleDelete = (id) => {
        const item = restaurants.find(r => r.id === id);
        if (confirm(`Bạn có chắc chắn muốn xóa nhà hàng "${item.name}" không?`)) {
            restaurants = restaurants.filter(r => r.id !== id);
            renderTable(restaurants);
            updateStats();
            // Trong thực tế: Gọi API DELETE /api/restaurants/{id}
        }
    };

    window.handleApprove = (id) => {
        const index = restaurants.findIndex(r => r.id === id);
        if (index !== -1) {
            if (confirm(`Phê duyệt nhà hàng "${restaurants[index].name}" hoạt động?`)) {
                restaurants[index].status = 'active';
                // Gán rating mặc định nếu muốn
                if(!restaurants[index].rating) restaurants[index].rating = 5.0;

                renderTable(restaurants);
                updateStats();
                // Trong thực tế: Gọi API PUT /api/restaurants/{id}/approve
            }
        }
    };

    // 6. INITIALIZATION
    renderTable(restaurants);
    updateStats();
});