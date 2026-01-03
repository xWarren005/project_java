// Danh sách các ngân hàng phổ biến
const banksList = [
    { id: "VCB", name: "Vietcombank", bin: "970436" },
    { id: "MB", name: "MB Bank", bin: "970422" },
    { id: "TCB", name: "Techcombank", bin: "970407" },
    { id: "ACB", name: "ACB", bin: "970416" },
    { id: "VPB", name: "VPBank", bin: "970432" },
    { id: "BIDV", name: "BIDV", bin: "970418" },
    { id: "ICB", name: "VietinBank", bin: "970415" },
    { id: "TPB", name: "TPBank", bin: "970423" }
];

document.addEventListener("DOMContentLoaded", () => {
    populateBanks();
    fetchCurrentConfig(); // Tải cấu hình từ Server khi vào trang
});

// 1. Đổ dữ liệu vào Select Box
function populateBanks() {
    const select = document.getElementById("bank-select");
    select.innerHTML = '<option value="">-- Chọn ngân hàng --</option>'; // Reset
    banksList.forEach(bank => {
        const option = document.createElement("option");
        option.value = bank.id;
        option.text = `${bank.id} - ${bank.name}`;
        select.appendChild(option);
    });
}

// 2. Tải cấu hình hiện tại từ Database
async function fetchCurrentConfig() {
    try {
        const response = await fetch('/api/manager/qr-config');
        if (response.ok) {
            const data = await response.json();

            // Nếu có dữ liệu, điền vào form
            if (data.bankId) {
                document.getElementById("bank-select").value = data.bankId;
                document.getElementById("acc-no").value = data.accountNo;
                document.getElementById("acc-name").value = data.accountName;
                document.getElementById("content").value = data.content || "Thanh toan";

                // Cập nhật giao diện preview
                updatePreview(data.bankId, data.accountNo, data.accountName, data.content);
            }
        }
    } catch (error) {
        console.error("Lỗi tải cấu hình QR:", error);
    }
}

// 3. Hàm xử lý khi submit form (Lưu vào DB + Gen QR)
async function generateQR(e) {
    if(e) e.preventDefault();

    // Lấy dữ liệu từ form
    const bankId = document.getElementById("bank-select").value;
    const accNo = document.getElementById("acc-no").value;
    const accName = document.getElementById("acc-name").value.toUpperCase();
    const content = document.getElementById("content").value;

    if (!bankId || !accNo || !accName) {
        alert("Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    // Tạo object DTO để gửi về server
    const configDTO = {
        bankId: bankId,
        accountNo: accNo,
        accountName: accName,
        content: content,
        template: "compact2"
    };

    // Gọi API Lưu xuống DB
    try {
        const response = await fetch('/api/manager/qr-config', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(configDTO)
        });

        if (response.ok) {
            alert("Đã cập nhật mã QR thành công!");
            updatePreview(bankId, accNo, accName, content);
        } else {
            alert("Lỗi khi lưu cấu hình!");
        }
    } catch (error) {
        console.error("Lỗi:", error);
        alert("Có lỗi xảy ra khi kết nối server");
    }
}

// 4. Hàm cập nhật giao diện Preview (Client-side generation)
function updatePreview(bankId, accNo, accName, content) {
    // Cập nhật text
    const bankName = banksList.find(b => b.id === bankId)?.name || bankId;
    document.getElementById("view-bank").textContent = bankName;
    document.getElementById("view-acc").textContent = accNo;
    document.getElementById("view-name").textContent = accName;
    document.getElementById("view-content").textContent = content;

    // Tạo link VietQR
    const template = "compact2";
    const apiUrl = `https://img.vietqr.io/image/${bankId}-${accNo}-${template}.png?accountName=${encodeURIComponent(accName)}&addInfo=${encodeURIComponent(content)}`;

    // Gán vào ảnh
    const imgEl = document.getElementById("qr-image");
    imgEl.src = apiUrl;
}

// 5. Hàm tải xuống
function downloadQR() {
    const imgUrl = document.getElementById("qr-image").src;
    // Mở tab mới để tải
    window.open(imgUrl, '_blank');
}