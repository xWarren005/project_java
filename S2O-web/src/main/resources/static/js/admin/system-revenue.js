document.addEventListener('DOMContentLoaded', () => {

    // DOM ELEMENTS
    const tableBody = document.getElementById('revenueTableBody');
    const searchInput = document.getElementById('searchInput');

    const elToday = document.getElementById('sys-today');
    const elMonth = document.getElementById('sys-month');
    const elYear = document.getElementById('sys-year');

    let restaurantList = [];

    // Helper: Format tiền tệ
    const formatCurrency = (amount) => {
        if (!amount && amount !== 0) return '0 đ';
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    };

    // 1. LOAD DATA TỪ API
    async function loadRevenueData() {
        try {
            const res = await fetch('/api/admin/revenue');
            if(res.ok) {
                const data = await res.json();

                // Cập nhật 3 ô thống kê
                if(elToday) elToday.innerText = formatCurrency(data.systemRevenueToday);
                if(elMonth) elMonth.innerText = formatCurrency(data.systemRevenueMonth);
                if(elYear) elYear.innerText = formatCurrency(data.systemRevenueYear);

                // Lưu danh sách nhà hàng
                restaurantList = data.restaurantStats;

                // Xóa ô tìm kiếm
                if(searchInput) searchInput.value = "";

                renderTable(restaurantList);
            } else {
                console.error("Lỗi API Revenue");
            }
        } catch(e) { console.error(e); }
    }

    // 2. RENDER TABLE (Đơn giản hóa)
    const renderTable = (data) => {
        tableBody.innerHTML = '';

        if (!data || data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="3" style="text-align:center; padding:24px; color:#888">Chưa có doanh thu tháng này.</td></tr>`;
            return;
        }

        data.forEach(item => {
            const row = document.createElement('tr');

            // item bao gồm: restaurantName, monthlyRevenue, commission

            row.innerHTML = `
                <td class="fw-bold" style="font-size: 15px;">
                    <i data-lucide="store" style="width: 16px; margin-right: 8px; vertical-align: middle;"></i>
                    ${item.restaurantName}
                </td>
                <td class="fw-bold">${formatCurrency(item.monthlyRevenue)}</td>
                <td class="text-green fw-bold">+${formatCurrency(item.commission)}</td>
            `;
            tableBody.appendChild(row);
        });

        // Render icon
        if (typeof lucide !== 'undefined') lucide.createIcons();
    };

    // 3. TÌM KIẾM THEO TÊN NHÀ HÀNG
    if(searchInput) {
        searchInput.addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase().trim();
            const filtered = restaurantList.filter(item => {
                return item.restaurantName.toLowerCase().includes(term);
            });
            renderTable(filtered);
        });
    }

    // KHỞI CHẠY
    loadRevenueData();
});