/**
 * Bella Couture - Live Chat System (Bản Hoàn Chỉnh Tuyệt Đối)
 */

let stompClient = null;
let conversationId = null;
let currentChatStatus = "AI";

// 💥 HÀM TOÀN CỤC KHI CLICK BẬT/TẮT KHUNG CHAT
window.toggleBridalChatPopup = function () {
    if (!conversationId) {
        fetch('/api/chat/get-or-create')
            .then(response => {
                if (!response.ok) throw new Error("Chưa đăng nhập");
                return response.json();
            })
            .then(data => {
                conversationId = data.conversationId;
                currentChatStatus = data.status || "AI";
                console.log("Đã kết nối phòng chat số: " + conversationId + " [Trạng thái: " + currentChatStatus + "]");

                openAndConnectChatPopup();
                loadChatHistory(conversationId);
            })
            .catch(error => {
                console.error("Lỗi luồng khởi tạo chat: ", error);
                alert("Vui lòng đăng nhập tài khoản để trò chuyện cùng trợ lý Bella nhé!");
            });
    } else {
        const chatWrapper = document.getElementById('bridalChatWrapper');
        if (chatWrapper) {
            const isHidden = chatWrapper.style.display === 'none';
            chatWrapper.style.display = isHidden ? 'flex' : 'none';

            if (isHidden) {
                const chatMessageArea = document.getElementById('chatMessageArea');
                if (chatMessageArea) chatMessageArea.scrollTop = chatMessageArea.scrollHeight;
            }
        }
    }
};

// 🛠️ HÀM DỰNG GIAO DIỆN KHUNG CHAT POPUP
function openAndConnectChatPopup() {
    let chatWrapper = document.getElementById('bridalChatWrapper');

    if (!chatWrapper) {
        const chatHtml = `
            <div id="bridalChatWrapper" style="display: flex; position: fixed; bottom: 90px; right: 30px; width: 360px; height: 480px; background: #fdfbf7; border: 2px solid #c5a880; box-shadow: 0 10px 30px rgba(0,0,0,0.15); z-index: 9999; font-family: 'Plus Jakarta Sans', sans-serif; flex-direction: column;">

                <div style="background: #1a1a1a; color: #fff; padding: 12px 15px; display: flex; justify-content: space-between; align-items: center;">
                    <div class="d-flex align-items-center gap-2">
                        <div style="width: 8px; height: 8px; background: #2ecc71; border-radius: 50%;"></div>
                        <strong style="font-family: 'Playfair Display', serif; letter-spacing: 1px; color: #fff; font-size: 0.95rem;">Bella Couture Assistant</strong>
                    </div>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <button type="button" id="requestAdminBtn" style="background: #c5a880; border: none; color: #fff; padding: 4px 10px; font-size: 0.75rem; cursor: pointer; border-radius: 20px; font-weight: bold; transition: all 0.3s;">
                            Gặp nhân viên 📞
                        </button>
                        <button type="button" id="closeChatBtn" style="background: none; border: none; color: #fff; cursor: pointer; font-size: 1.2rem;"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                </div>

                <div id="chatMessageArea" style="flex: 1; padding: 15px; overflow-y: auto; display: flex; flex-direction: column; gap: 10px; background: #fcfaf2;">
                    <div id="defaultWelcomeMsg" style="align-self: flex-start; background: #eaddce; color: #1a1a1a; padding: 8px 12px; max-width: 80%; font-size: 0.9rem; display: block; margin-bottom: 5px;">
                        Xin chào nàng dâu mới! Bella có thể giúp gì cho nàng trong việc tư vấn mẫu thiết kế và dịch vụ cưới ạ? ✨
                    </div>
                </div>

                <div style="padding: 10px; border-top: 1px solid #eaddce; display: flex; gap: 8px; background: #fff;">
                    <input type="text" id="chatInputTxt" placeholder="Nhập tin nhắn gửi Bella..." style="flex: 1; border: 1px solid #c5a880; padding: 8px 12px; font-size: 0.9rem; outline: none;">
                    <button type="button" id="sendChatBtn" style="background: #c5a880; border: none; color: #fff; padding: 0 15px; cursor: pointer;"><i class="fa-solid fa-paper-plane"></i></button>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', chatHtml);

        document.getElementById('closeChatBtn').addEventListener('click', () => {
            document.getElementById('bridalChatWrapper').style.display = 'none';
        });
        document.getElementById('sendChatBtn').addEventListener('click', sendBridalMessage);
        document.getElementById('chatInputTxt').addEventListener('keypress', function (e) {
            if (e.key === 'Enter') sendBridalMessage();
        });

        document.getElementById('requestAdminBtn').addEventListener('click', handleChatModeToggle);

        // Render giao diện nút ban đầu từ DB trả về
        renderToggleBtnUI(currentChatStatus);
    } else {
        chatWrapper.style.display = 'flex';
    }

    initWebSocketConnection();
}

// 🌐 KHỞI TẠO VÀ ĐĂNG KÝ LẮNG NGHE KÊNH WEBSOCKET
function initWebSocketConnection() {
    if (stompClient !== null && stompClient.connected) return;

    const socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        console.log('Connected WebSocket thành công phòng: ' + conversationId);

        // Đăng ký nhận tin nhắn chat
        stompClient.subscribe('/topic/chat/' + conversationId, function (response) {
            const messageObj = JSON.parse(response.body);
            appendMessageToUI(messageObj);
        });

        // 🌟 NƠI DUY NHẤT ĐƯỢC PHÉP ĐỔI TRẠNG THÁI NÚT: Khi nhận lệnh chính thức từ Server trả về
        stompClient.subscribe('/topic/chat/status/' + conversationId, function (response) {
            const statusUpdate = JSON.parse(response.body);
            if (statusUpdate && statusUpdate.status) {
                currentChatStatus = statusUpdate.status;
                renderToggleBtnUI(currentChatStatus);
            }
        });

    }, function (error) {
        console.error('Lỗi kết nối mạng WebSocket: ', error);
    });
}

// ✉️ HÀM ĐẨY TIN NHẮN MỚI CỦA KHÁCH LÊN BACKEND
function sendBridalMessage() {
    const inputElement = document.querySelector('#chatInputTxt');
    if (!inputElement) return;

    const contentText = inputElement.value.trim();

    if (contentText && stompClient && stompClient.connected) {
        const msgPayload = {
            content: contentText,
            senderType: 'CUSTOMER',
            messageType: 'TEXT'
        };

        stompClient.send("/app/chat/send/" + conversationId, {}, JSON.stringify(msgPayload));
        inputElement.value = '';
    }
}

// 🔀 HÀM XỬ LÝ ĐIỀU HƯỚNG (ĐÃ FIX CHỐNG NHẢY LOẠN): Không tự gán biến bừa bãi trước khi API chạy xong
function handleChatModeToggle() {
    if (!stompClient || !stompClient.connected || !conversationId) {
        alert("Đường truyền mạng đang gián đoạn, nàng vui lòng thử lại sau giây lát!");
        return;
    }

    // Nếu đang ở luồng AI -> Click để xin gặp Admin
    if (currentChatStatus === "AI") {
        fetch(`/api/chat/toggle-status?id=${conversationId}&status=WAITING_ADMIN`, { method: 'POST' })
            .then(res => {
                if (res.ok) {
                    stompClient.send("/app/chat/request-admin/" + conversationId, {}, {});
                    console.log("Đã phát lệnh yêu cầu gặp chuyên viên tư vấn thật.");
                }
            });
    }
    // Nếu đang được Nhân viên trực hoặc đang chờ -> Bấm để trả luồng tư vấn về cho Máy AI
    else if (currentChatStatus === "ADMIN_SUPPORT" || currentChatStatus === "WAITING_ADMIN") {
        fetch(`/api/chat/toggle-status?id=${conversationId}&status=AI`, { method: 'POST' })
            .then(res => {
                if (res.ok) {
                    stompClient.send("/app/chat/request-admin/" + conversationId, {}, {});
                    console.log("Đã trả luồng hội thoại thành công về cho Trợ lý ảo AI.");
                }
            });
    }
}

// 🎨 HÀM VẼ GIAO DIỆN NÚT BẤM THEO TRẠNG THÁI HIỆN TẠI
function renderToggleBtnUI(status) {
    const adminBtn = document.getElementById('requestAdminBtn');
    if (!adminBtn) return;

    if (status === "AI") {
        adminBtn.innerHTML = "Gặp nhân viên 📞";
        adminBtn.style.background = "#c5a880";
        adminBtn.style.color = "#fff";
        adminBtn.style.border = "none";
    } else if (status === "WAITING_ADMIN") {
        adminBtn.innerHTML = "Đang kết nối... ⏳";
        adminBtn.style.background = "#e67e22";
        adminBtn.style.color = "#fff";
        adminBtn.style.border = "none";
    } else if (status === "ADMIN_SUPPORT") {
        adminBtn.innerHTML = "AI tư vấn 🤖";
        adminBtn.style.background = "transparent";
        adminBtn.style.color = "#c5a880";
        adminBtn.style.border = "1px solid #c5a880";
    }
}

// 🌟 HÀM TẢI LỊCH SỬ TIN NHẮN CŨ TỪ DATABASE LÊN KHUNG CHAT
function loadChatHistory(id) {
    fetch('/api/chat/history/' + id)
        .then(response => {
            if (!response.ok) throw new Error("Không thể đọc dữ liệu");
            return response.json();
        })
        .then(messages => {
            const chatMessageArea = document.getElementById('chatMessageArea');
            if (!chatMessageArea || !messages) return;

            messages.forEach(msg => {
                appendMessageToUI(msg);
            });
            console.log("Đã nạp thành công " + messages.length + " tin nhắn lịch sử.");
        })
        .catch(error => {
            console.error("Lỗi khi tải lịch sử chat:", error);
        });
}

// 💥 HÀM VẼ BONG BÓNG TIN NHẮN LÊN GIAO DIỆN CHAT (ĐÃ TỐI ƯU CHỐNG TRÙNG TIN)
function appendMessageToUI(message) {
    const chatMessageArea = document.getElementById('chatMessageArea');
    if (!chatMessageArea || !message.content) return;

    const existingRows = chatMessageArea.querySelectorAll('.chat-row-item');
    if (existingRows.length > 0) {
        const lastRow = existingRows[existingRows.length - 1];
        const lastBubble = lastRow.querySelector('.msg-bubble');
        if (lastBubble && lastBubble.innerText === message.content && message.senderType === 'CUSTOMER') {
            return;
        }
    }

    const rowDiv = document.createElement('div');
    rowDiv.classList.add('chat-row-item');
    rowDiv.style.width = '100%';
    rowDiv.style.display = 'flex';
    rowDiv.style.marginBottom = '5px';

    const msgBubble = document.createElement('div');
    msgBubble.classList.add('msg-bubble');
    msgBubble.style.padding = '8px 12px';
    msgBubble.style.maxWidth = '80%';
    msgBubble.style.fontSize = '0.9rem';
    msgBubble.style.wordBreak = 'break-word';
    msgBubble.innerText = message.content;

    if (message.senderType === 'CUSTOMER') {
        rowDiv.style.justifyContent = 'flex-end';
        msgBubble.style.background = '#1a1a1a';
        msgBubble.style.color = '#fff';
    } else {
        rowDiv.style.justifyContent = 'flex-start';
        msgBubble.style.background = '#eaddce';
        msgBubble.style.color = '#1a1a1a';
    }

    rowDiv.appendChild(msgBubble);
    chatMessageArea.appendChild(rowDiv);
    chatMessageArea.scrollTop = chatMessageArea.scrollHeight;
}