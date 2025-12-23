// Danh sách các ngân hàng phổ biến (Mã BIN/ID VietQR)
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

    // Tải dữ liệu mặc định (nếu muốn demo luôn)
    generateQR({ preventDefault: () => {} });
});

// 1. Đổ dữ liệu vào Select Box
function populateBanks() {
    const select = document.getElementById("bank-select");
    banksList.forEach(bank => {
        const option = document.createElement("option");
        option.value = bank.id;
        option.text = `${bank.id} - ${bank.name}`;
        // Set mặc định là VCB để demo
        if(bank.id === 'VCB') option.selected = true;
        select.appendChild(option);
    });
}

// 2. Hàm tạo QR
function generateQR(e) {
    if(e) e.preventDefault(); // Chặn reload trang

    // Lấy dữ liệu từ form
    const bankId = document.getElementById("bank-select").value || "VCB";
    const accNo = document.getElementById("acc-no").value || "1234567890";
    const accName = document.getElementById("acc-name").value || "NGUYEN VAN QUAN LY";
    const content = document.getElementById("content").value || "Thanh toan";

    // Cập nhật thông tin hiển thị bên phải
    document.getElementById("view-bank").textContent = banksList.find(b => b.id === bankId)?.name || bankId;
    document.getElementById("view-acc").textContent = accNo;
    document.getElementById("view-name").textContent = accName.toUpperCase();
    document.getElementById("view-content").textContent = content;

    // TẠO URL VIETQR (Quick Link API)
    // Cấu trúc: https://img.vietqr.io/image/<BANK_ID>-<ACC_NO>-<TEMPLATE>.png?accountName=<NAME>&addInfo=<CONTENT>
    // Template 'compact2' hoặc 'qr_only' hoặc 'print'
    const template = "compact2";
    const apiUrl = `https://img.vietqr.io/image/${bankId}-${accNo}-${template}.png?accountName=${encodeURIComponent(accName)}&addInfo=${encodeURIComponent(content)}`;

    // Gán vào ảnh
    const imgEl = document.getElementById("qr-image");
    imgEl.src = apiUrl;
}

// 3. Hàm tải xuống
function downloadQR() {
    const imgUrl = document.getElementById("qr-image").src;
    // Mở tab mới để tải (đơn giản nhất)
    window.open(imgUrl, '_blank');
}