document.addEventListener('DOMContentLoaded', () => {

    // =========================================
    // 1. MOCK DATA (Dữ liệu giả lập)
    // =========================================
    const transactions = [
        {
            id: 1,
            date: '2024-01-15',
            restaurant: 'Phở 24',
            package: 'Premium',
            amount: 299,
            commission: 45
        },
        {
            id: 2,
            date: '2024-01-15',
            restaurant: 'Sushi World',
            package: 'Enterprise',
            amount: 599,
            commission: 90
        },
        {
            id: 3,
            date: '2024-01-14',
            restaurant: 'BBQ House',
            package: 'Basic',
            amount: 99,
            commission: 15
        },
        {
            id: 4,
            date: '2024-01-14',
            restaurant: 'Vegan Garden',
            package: 'Premium',
            amount: 299,
            commission: 45
        },
        {
            id: 5,
            date: '2024-01-13',
            restaurant: 'Pizza Express',
            package: 'Basic',
            amount: 99,
            commission: 15
        },
        {
            id: 6,
            date: '2024-01-12',
            restaurant: 'Burger King',
            package: 'Enterprise',
            amount: 599,
            commission: 90
        },
        {
            id: 7,
            date: '2024-01-11',
            restaurant: 'Kichi Kichi',
            package: 'Premium',
            amount: 299,
            commission: 45
        },
        {
            id: 8,
            date: '2024-01-10',
            restaurant: 'The Coffee House',
            package: 'Basic',
            amount: 99,
            commission: 15
        }
    ];

    // =========================================
    // 2. DOM ELEMENTS
    // =========================================
    const tableBody = document.querySelector('#revenueTable tbody');
    const searchInput = document.getElementById('searchInput');

    // =========================================
    // 3. HELPER FUNCTIONS
    // =========================================

    // Format tiền tệ ($)
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 0
        }).format(amount);
    };

    // Lấy class badge dựa trên tên gói
    const getBadgeClass = (pkg) => {
        const lowerPkg = pkg.toLowerCase();
        if (lowerPkg === 'enterprise') return 'badge-enterprise';
        if (lowerPkg === 'premium') return 'badge-premium';
        return 'badge-basic';
    };

    // =========================================
    // 4. RENDER FUNCTION
    // =========================================
    const renderTable = (data) => {
        tableBody.innerHTML = ''; // Xóa dữ liệu cũ (hoặc dữ liệu tĩnh trong HTML)

        if (data.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align: center; padding: 24px; color: #6b7280;">
                        Không tìm thấy giao dịch nào phù hợp.
                    </td>
                </tr>
            `;
            return;
        }

        data.forEach(item => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${item.date}</td>
                <td class="fw-bold">${item.restaurant}</td>
                <td>
                    <span class="badge ${getBadgeClass(item.package)}">
                        ${item.package}
                    </span>
                </td>
                <td class="fw-bold">${formatCurrency(item.amount)}/tháng</td>
                <td class="text-green fw-bold">+${formatCurrency(item.commission)}</td>
            `;

            tableBody.appendChild(row);
        });
    };

    // =========================================
    // 5. EVENT LISTENERS
    // =========================================

    // Xử lý tìm kiếm
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase().trim();

        const filteredData = transactions.filter(item => {
            return (
                item.restaurant.toLowerCase().includes(searchTerm) ||
                item.package.toLowerCase().includes(searchTerm) ||
                item.date.includes(searchTerm)
            );
        });

        renderTable(filteredData);
    });

    // =========================================
    // 6. INITIALIZATION
    // =========================================

    // Render dữ liệu lần đầu khi tải trang
    renderTable(transactions);

    // Re-init Icons (nếu dùng dynamic content đôi khi cần gọi lại,
    // nhưng ở đây script lucide ở cuối body HTML chính sẽ lo việc này)
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});