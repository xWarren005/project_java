function toggleAI() {
    const box = document.getElementById("ai-chat-box");
    box.classList.toggle("hidden");
}

function addMsg(text, type) {
    const box = document.getElementById("ai-messages");
    const div = document.createElement("div");
    div.className = type;
    div.innerText = text;
    box.appendChild(div);
    box.scrollTop = box.scrollHeight;
}

function sendAI() {
    const input = document.getElementById("ai-input");
    const text = input.value.trim();
    if (!text) return;

    addMsg(text, "user");
    input.value = "";

    fetch("/api/ai/chatbot", {
        method: "POST",
        headers: {"Content-Type":"application/json"},
        body: JSON.stringify({
            question: text,
            restaurantId: window.currentRestaurantId || 1
        })
    })
        .then(r => r.text())
        .then(ans => addMsg(ans, "bot"))
        .catch(() => addMsg("Không kết nối được AI.", "bot"));
}
