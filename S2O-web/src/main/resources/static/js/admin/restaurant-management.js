document.addEventListener('DOMContentLoaded', () => {
    // 1. DATA (Dữ liệu mẫu)
    let restaurants = [
        { id: 1, name: "Phở 24", address: "123 Lê Lợi, Q1, TP.HCM", status: "active", rating: 4.5 },
        { id: 2, name: "Sushi World", address: "456 Nguyễn Huệ, Q3, TP.HCM", status: "active", rating: 4.8 },
        { id: 3, name: "BBQ House", address: "789 Võ Văn Tần, Q3, TP.HCM", status: "pending", rating: null },
        { id: 4, name: "Vegan Garden", address: "321 Pasteur, Q7, TP.HCM", status: "active", rating: 4.3 },
        { id: 5, name: "Pizza Express", address: "654 Hai Bà Trưng, Q1, TP.HCM", status: "inactive", rating: 4.1 }
    ];

    const tableBody = document.getElementById('restaurantTableBody');
    const searchInput = document.querySelector('.search-input');

    // --- KHAI BÁO CÁC BIẾN MODAL (PHẦN BẠN BỊ THIẾU) ---
    const addModal = document.getElementById('addModal');
    const inputName = document.getElementById('newName');
    const inputAddress = document.getElementById('newAddress');

    // 2. HELPER FUNCTIONS
    const removeVietnameseTones = (str) => {
        str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g,"a");
        str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g,"e");
        str = str.replace(/ì|í|ị|ỉ|ĩ/g,"i");
        str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g,"o");
        str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u");
        str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y");
        str = str.replace(/đ/g,"d");
        str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A");
        str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E");
        str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I");
        str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O");
        str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U");
        str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y");
        str = str.replace(/Đ/g, "D");
        return str.normalize('NFD').replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").replace(/Đ/g, "D");
    }

    const highlightKeyword = (text, keyword) => {
        if (!keyword) return text;
        const regex = new RegExp(`(${keyword})`, 'gi');
        return text.replace(regex, '<span class="highlight-text">$1</span>');
    };

    const getStatusBadge = (status, id) => {
        switch(status) {
            case 'active':
                return '<span class="badge badge-active">Hoạt động</span>';
            case 'pending':
                return `<button class="badge-approve-btn" onclick="handleApprove(${id})" title="Nhấn để duyệt ngay">
                            Chờ duyệt
                        </button>`;
            case 'inactive':
                return '<span class="badge badge-inactive">Ngừng hoạt động</span>';
            default:
                return '<span class="badge badge-inactive">Không xác định</span>';
        }
    };

    const getRatingHtml = (rating) => {
        if (!rating) return '<span style="color: #9ca3af; font-size: 0.875rem;">N/A</span>';
        return `<span class="rating">${rating} <i data-lucide="star" class="icon-star"></i></span>`;
    };

    // 3. RENDER TABLE
    const renderTable = (data, keyword = '') => {
        tableBody.innerHTML = '';
        if (data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 30px; color: #6b7280;">Không tìm thấy kết quả</td></tr>`;
            return;
        }

        data.forEach(item => {
            const row = document.createElement('tr');
            const displayName = highlightKeyword(item.name, keyword);
            const displayAddress = item.address;

            let actionButtons = `
                <button class="btn-action btn-edit" onclick="handleEdit(${item.id})"><i data-lucide="pencil"></i></button>
                <button class="btn-action btn-delete" onclick="handleDelete(${item.id})"><i data-lucide="trash-2"></i></button>
            `;

            row.innerHTML = `
                <td class="fw-bold">${displayName}</td>
                <td>${displayAddress}</td>
                <td>${getStatusBadge(item.status, item.id)}</td>
                <td>${getRatingHtml(item.rating)}</td>
                <td><div class="action-buttons">${actionButtons}</div></td>
            `;
            tableBody.appendChild(row);
        });
        lucide.createIcons();
    };

    // 4. EVENTS
    searchInput.addEventListener('input', (e) => {
        const rawKeyword = e.target.value;
        const searchTerm = removeVietnameseTones(rawKeyword.trim().toLowerCase());
        const filtered = restaurants.filter(r => {
            const name = removeVietnameseTones(r.name.toLowerCase());
            return name.startsWith(searchTerm);
        });
        renderTable(filtered, rawKeyword);
    });

    // 5. GLOBAL HANDLERS (LOGIC MODAL & ACTIONS)

    // --- Logic Mở/Đóng Modal (PHẦN BẠN BỊ THIẾU) ---
    window.openAddModal = () => {
        if(addModal) {
            addModal.classList.add('show');
            if(inputName) inputName.focus();
        } else {
            console.error("Không tìm thấy modal có id='addModal'");
        }
    };

    window.closeAddModal = () => {
        if(addModal) {
            addModal.classList.remove('show');
            inputName.value = '';
            inputAddress.value = '';
        }
    };

    // --- Logic Lưu Nhà Hàng Mới (PHẦN BẠN BỊ THIẾU) ---
    window.handleSaveNew = () => {
        const nameVal = inputName.value.trim();
        const addressVal = inputAddress.value.trim();

        if (!nameVal || !addressVal) {
            alert("Vui lòng nhập đầy đủ Tên và Địa chỉ!");
            return;
        }

        const newRestaurant = {
            id: Date.now(),
            name: nameVal,
            address: addressVal,
            status: 'pending',
            rating: null
        };

        restaurants.unshift(newRestaurant);
        renderTable(restaurants, searchInput.value);
        closeAddModal();
    };

    // --- Logic Sửa/Xóa/Duyệt (Cũ) ---
    window.handleEdit = (id) => {
        alert(`Chỉnh sửa nhà hàng ID: ${id}`);
    };

    window.handleApprove = (id) => {
        if(event) event.stopPropagation();
        if(confirm(`Xác nhận duyệt nhà hàng (ID: ${id})?`)) {
            const item = restaurants.find(r => r.id === id);
            if (item) {
                item.status = 'active';
                item.rating = 5.0;
                searchInput.dispatchEvent(new Event('input'));
            }
        }
    };

    window.handleDelete = (id) => {
        if(confirm('Bạn có chắc chắn muốn xóa nhà hàng này?')) {
            restaurants = restaurants.filter(r => r.id !== id);
            searchInput.dispatchEvent(new Event('input'));
        }
    };

    // Khởi tạo
    renderTable(restaurants);
});document.addEventListener('DOMContentLoaded', () => {
       // 1. DATA (Dữ liệu mẫu)
       let restaurants = [
           { id: 1, name: "Phở 24", address: "123 Lê Lợi, Q1, TP.HCM", status: "active", rating: 4.5 },
           { id: 2, name: "Sushi World", address: "456 Nguyễn Huệ, Q3, TP.HCM", status: "active", rating: 4.8 },
           { id: 3, name: "BBQ House", address: "789 Võ Văn Tần, Q3, TP.HCM", status: "pending", rating: null },
           { id: 4, name: "Vegan Garden", address: "321 Pasteur, Q7, TP.HCM", status: "active", rating: 4.3 },
           { id: 5, name: "Pizza Express", address: "654 Hai Bà Trưng, Q1, TP.HCM", status: "inactive", rating: 4.1 }
       ];

       const tableBody = document.getElementById('restaurantTableBody');
       const searchInput = document.querySelector('.search-input');

       // --- KHAI BÁO CÁC BIẾN MODAL (PHẦN BẠN BỊ THIẾU) ---
       const addModal = document.getElementById('addModal');
       const inputName = document.getElementById('newName');
       const inputAddress = document.getElementById('newAddress');

       // 2. HELPER FUNCTIONS
       const removeVietnameseTones = (str) => {
           str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g,"a");
           str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g,"e");
           str = str.replace(/ì|í|ị|ỉ|ĩ/g,"i");
           str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g,"o");
           str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u");
           str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y");
           str = str.replace(/đ/g,"d");
           str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A");
           str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E");
           str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I");
           str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O");
           str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U");
           str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y");
           str = str.replace(/Đ/g, "D");
           return str.normalize('NFD').replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").replace(/Đ/g, "D");
       }

       const highlightKeyword = (text, keyword) => {
           if (!keyword) return text;
           const regex = new RegExp(`(${keyword})`, 'gi');
           return text.replace(regex, '<span class="highlight-text">$1</span>');
       };

       const getStatusBadge = (status, id) => {
           switch(status) {
               case 'active':
                   return '<span class="badge badge-active">Hoạt động</span>';
               case 'pending':
                   return `<button class="badge-approve-btn" onclick="handleApprove(${id})" title="Nhấn để duyệt ngay">
                               Chờ duyệt
                           </button>`;
               case 'inactive':
                   return '<span class="badge badge-inactive">Ngừng hoạt động</span>';
               default:
                   return '<span class="badge badge-inactive">Không xác định</span>';
           }
       };

       const getRatingHtml = (rating) => {
           if (!rating) return '<span style="color: #9ca3af; font-size: 0.875rem;">N/A</span>';
           return `<span class="rating">${rating} <i data-lucide="star" class="icon-star"></i></span>`;
       };

       // 3. RENDER TABLE
       const renderTable = (data, keyword = '') => {
           tableBody.innerHTML = '';
           if (data.length === 0) {
               tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding: 30px; color: #6b7280;">Không tìm thấy kết quả</td></tr>`;
               return;
           }

           data.forEach(item => {
               const row = document.createElement('tr');
               const displayName = highlightKeyword(item.name, keyword);
               const displayAddress = item.address;

               let actionButtons = `
                   <button class="btn-action btn-edit" onclick="handleEdit(${item.id})"><i data-lucide="pencil"></i></button>
                   <button class="btn-action btn-delete" onclick="handleDelete(${item.id})"><i data-lucide="trash-2"></i></button>
               `;

               row.innerHTML = `
                   <td class="fw-bold">${displayName}</td>
                   <td>${displayAddress}</td>
                   <td>${getStatusBadge(item.status, item.id)}</td>
                   <td>${getRatingHtml(item.rating)}</td>
                   <td><div class="action-buttons">${actionButtons}</div></td>
               `;
               tableBody.appendChild(row);
           });
           lucide.createIcons();
       };

       // 4. EVENTS
       searchInput.addEventListener('input', (e) => {
           const rawKeyword = e.target.value;
           const searchTerm = removeVietnameseTones(rawKeyword.trim().toLowerCase());
           const filtered = restaurants.filter(r => {
               const name = removeVietnameseTones(r.name.toLowerCase());
               return name.startsWith(searchTerm);
           });
           renderTable(filtered, rawKeyword);
       });

       // 5. GLOBAL HANDLERS (LOGIC MODAL & ACTIONS)

       // --- Logic Mở/Đóng Modal (PHẦN BẠN BỊ THIẾU) ---
       window.openAddModal = () => {
           if(addModal) {
               addModal.classList.add('show');
               if(inputName) inputName.focus();
           } else {
               console.error("Không tìm thấy modal có id='addModal'");
           }
       };

       window.closeAddModal = () => {
           if(addModal) {
               addModal.classList.remove('show');
               inputName.value = '';
               inputAddress.value = '';
           }
       };

       // --- Logic Lưu Nhà Hàng Mới (PHẦN BẠN BỊ THIẾU) ---
       window.handleSaveNew = () => {
           const nameVal = inputName.value.trim();
           const addressVal = inputAddress.value.trim();

           if (!nameVal || !addressVal) {
               alert("Vui lòng nhập đầy đủ Tên và Địa chỉ!");
               return;
           }

           const newRestaurant = {
               id: Date.now(),
               name: nameVal,
               address: addressVal,
               status: 'pending',
               rating: null
           };

           restaurants.unshift(newRestaurant);
           renderTable(restaurants, searchInput.value);
           closeAddModal();
       };

       // --- Logic Sửa/Xóa/Duyệt (Cũ) ---
       window.handleEdit = (id) => {
           alert(`Chỉnh sửa nhà hàng ID: ${id}`);
       };

       window.handleApprove = (id) => {
           if(event) event.stopPropagation();
           if(confirm(`Xác nhận duyệt nhà hàng (ID: ${id})?`)) {
               const item = restaurants.find(r => r.id === id);
               if (item) {
                   item.status = 'active';
                   item.rating = 5.0;
                   searchInput.dispatchEvent(new Event('input'));
               }
           }
       };

       window.handleDelete = (id) => {
           if(confirm('Bạn có chắc chắn muốn xóa nhà hàng này?')) {
               restaurants = restaurants.filter(r => r.id !== id);
               searchInput.dispatchEvent(new Event('input'));
           }
       };

       // Khởi tạo
       renderTable(restaurants);
   });