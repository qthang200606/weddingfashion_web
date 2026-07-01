/**
 * BELLA COUTURE - ADMIN REALTIME CHAT MOTOR
 */
let adminStompClient = null;
let isSubscribedToGlobal = false;

// Khởi chạy Socket ngay khi giao diện sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
    initAdminSocket();

    // Đăng ký sự kiện tương tác gửi tin nhắn
    document.getElementById('adminSendSocketBtn')?.addEventListener('click', executeAdminSend);
    document.getElementById('adminTextarea')?.addEventListener('keypress', function(e) {
        if(e.key === 'Enter') executeAdminSend();
    });

    // Luôn luôn cuộn khung chat xuống cuối cùng
    const container = document.getElementById('adminMsgContainer');
    if (container) container.scrollTop = container.scrollHeight;
});

/**
 * THIẾT LẬP KẾT NỐI SOCKET
 */
function initAdminSocket() {
    if (adminStompClient && adminStompClient.connected) return;

    const socket = new SockJS('/ws-chat');
    adminStompClient = Stomp.over(socket);
    adminStompClient.debug = null;

    adminStompClient.connect({}, function (frame) {
        // 1. Đăng ký kênh thông báo toàn hệ thống (Cập nhật Sidebar khi có khách mới)
        if (!isSubscribedToGlobal) {
            adminStompClient.subscribe('/topic/admin/notification', function (notification) {
                reloadRoomSidebarLists();
            });
            isSubscribedToGlobal = true;
        }

        // 2. Đăng ký nhận tin nhắn thời gian thực của phòng chat đang mở
        if (activeRoomId !== null) {
            adminStompClient.subscribe('/topic/chat/' + activeRoomId, function (output) {
                const msg = JSON.parse(output.body);
                appendRealtimeAdminBubble(msg);
            });
        }
    }, function(error) {
        // Tự động kết nối lại sau 5 giây nếu sập mạng ngầm
        setTimeout(initAdminSocket, 5000);
    });
}

/**
 * XỬ LÝ LỆNH GỬI TIN NHẮN
 */
function executeAdminSend() {
    const input = document.getElementById('adminTextarea');
    if (!input || input.disabled) return;

    const text = input.value.trim();
    if (!text || activeRoomId === null) return;

    const dataPacket = {
        senderType: 'ADMIN',
        senderId: currentAdminId,
        messageType: 'TEXT',
        content: text
    };

    adminStompClient.send("/app/chat/send/" + activeRoomId, {}, JSON.stringify(dataPacket));
    input.value = '';
}

/**
 * ĐẨY BONG BÓNG TIN NHẮN MỚI LÊN MÀN HÌNH (ĐÃ SỬA ĐỂ HIỂN THỊ ẢNH)
 */
function appendRealtimeAdminBubble(msg) {
    const container = document.getElementById('adminMsgContainer');
    if (!container) return;

    // Cơ chế chống trùng lặp tin nhắn cuối cùng của ADMIN vừa gửi
    const existingBubbles = container.querySelectorAll('.admin-bubble');
    if (existingBubbles.length > 0) {
        const lastBubble = existingBubbles[existingBubbles.length - 1];
        const lastText = lastBubble.querySelector('span:not(.bubble-tag):not(.bubble-time)')?.innerText;
        if (lastText === msg.content && msg.senderType === 'ADMIN') {
            return;
        }
    }

    let styleClass = 'customer';
    let labelText = 'Khách hàng';

    if (msg.senderType === 'ADMIN') {
        styleClass = 'admin';
        labelText = 'Bạn (Nhân viên)';
    } else if (msg.senderType === 'AI') {
        styleClass = 'ai';
        labelText = 'Trợ lý ảo AI';
    }

    // LÀM SẠCH TEXT TRƯỚC (BẢO MẬT XSS) RỒI MỚI DỊCH MÃ MARKDOWN THÀNH THẺ <img>
    const safeText = escapeHtml(msg.content);
    const finalContent = parseMarkdownImages(safeText);

    const html = `
        <div class="d-flex flex-column" style="width: 100%;">
            <div class="admin-bubble mb-2 ${styleClass}">
                <span class="bubble-tag">${labelText}</span>
                <span>${finalContent}</span> <!-- Thay vì hiển thị text thô, giờ hiển thị HTML chứa ảnh -->
                <span class="bubble-time">Vừa xong</span>
            </div>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', html);
    container.scrollTop = container.scrollHeight;
}

/**
 * AJAX CẬP NHẬT SIDEBAR KHÔNG RELOAD TRANG TRỰC TIẾP
 */
function reloadRoomSidebarLists() {
    fetch('/admin/api/chats/list')
        .then(res => {
            if(!res.ok) throw new Error();
            return res.json();
        })
        .then(rooms => {
            const conWait = document.getElementById('container-wait');
            const conActive = document.getElementById('container-active');
            const conAi = document.getElementById('container-ai');

            if(!conWait || !conActive || !conAi) return;

            conWait.innerHTML = '';
            conActive.innerHTML = '';
            conAi.innerHTML = '';

            let countWait = 0, countActive = 0, countAi = 0;

            rooms.forEach(room => {
                const isSelected = room.conversationId === activeRoomId ? 'active' : '';
                const customerName = room.user ? (room.user.fullName || "Khách Hàng") : "Khách Hàng";

                if (room.status === 'WAITING_ADMIN') {
                    countWait++;
                    let unreadBadge = room.unreadAdmin > 0 ? `<span class="badge bg-danger pulse-badge rounded-circle p-2">${room.unreadAdmin}</span>` : '';
                    conWait.insertAdjacentHTML('beforeend', `
                        <a href="/admin/chats?c=${room.conversationId}" class="room-card ${isSelected}">
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold text-dark"><i class="fa-regular fa-user me-2 text-danger"></i>${customerName}</span>
                                ${unreadBadge}
                            </div>
                            <small class="text-danger small fw-semibold d-block mt-1"><i class="fa-solid fa-bell me-1"></i> Yêu cầu chuyên viên hỗ trợ</small>
                        </a>
                    `);
                } else if (room.status === 'ADMIN_SUPPORT') {
                    countActive++;
                    conActive.insertAdjacentHTML('beforeend', `
                        <a href="/admin/chats?c=${room.conversationId}" class="room-card ${isSelected}">
                            <div class="fw-bold text-dark">${customerName}</div>
                            <small class="text-muted d-block mt-1">${room.subject || 'Chăm sóc may đo áo cưới'}</small>
                        </a>
                    `);
                } else if (room.status === 'AI') {
                    countAi++;
                    conAi.insertAdjacentHTML('beforeend', `
                        <a href="/admin/chats?c=${room.conversationId}" class="room-card ${isSelected}">
                            <div class="text-secondary fw-semibold"><i class="fa-solid fa-robot me-2 text-warning"></i> ${customerName}</div>
                        </a>
                    `);
                }
            });

            if(document.getElementById('count-wait')) document.getElementById('count-wait').innerText = countWait;
            if(document.getElementById('count-active')) document.getElementById('count-active').innerText = countActive;
            if(document.getElementById('count-ai')) document.getElementById('count-ai').innerText = countAi;

            document.getElementById('empty-wait').style.display = countWait === 0 ? 'block' : 'none';
            document.getElementById('empty-active').style.display = countActive === 0 ? 'block' : 'none';
            document.getElementById('empty-ai').style.display = countAi === 0 ? 'block' : 'none';
        })
        .catch(err => console.log("Đợi cập nhật vòng lặp kết nối..."));
}

/**
 * TIẾP QUẢN PHÒNG CHAT MƯỢT MÀ
 */
function executeTakeover(conversationId) {
    if (!conversationId) return;

    fetch('/admin/chat/takeover/' + conversationId, { method: 'POST' })
        .then(res => {
            if(res.ok) {
                const badge = document.getElementById('currentRoomStatusBadge');
                if(badge) badge.innerText = "ADMIN_SUPPORT";

                const inputArea = document.getElementById('adminTextarea');
                if (inputArea) {
                    inputArea.disabled = false;
                    inputArea.placeholder = "Soạn tin nhắn gửi phản hồi nhanh đến khách hàng...";
                }

                const sendBtn = document.getElementById('adminSendSocketBtn');
                if (sendBtn) sendBtn.disabled = false;

                const takeBtn = document.getElementById('takeoverBtn');
                if (takeBtn) takeBtn.style.display = 'none';

                reloadRoomSidebarLists();
            }
        })
        .catch(err => console.error(err));
}

/**
 * CHỐNG LỖI CHÈN MÃ ĐỘC (XSS)
 */
function escapeHtml(text) {
    if (!text) return '';
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

/**
 * 🌟 BỔ SUNG: BÓC TÁCH MÃ MARKDOWN ẢNH THÀNH THẺ HTML <img> THỰC TẾ
 */
/**
 * 🌟 NÂNG CẤP VẠN NĂNG: BÓC TÁCH MARKDOWN ẢNH KỂ CẢ KHI AI XUỐNG DÒNG (\n)
 */
function parseMarkdownImages(text) {
    if (!text) return '';

    return text.replace(
        /!\[(.*?)\]\((.*)\)/g,
        function (_, alt, url) {

            const cleanUrl = encodeURI(url.trim());

            return `
                <div class="chat-img-wrapper d-block mt-2">
                    <img
                        src="${cleanUrl}"
                        alt="${alt}"
                        style="
                            max-width:180px;
                            height:auto;
                            border-radius:8px;
                            border:1px solid #eee;
                            box-shadow:0 2px 5px rgba(0,0,0,0.1);
                        "
                    >
                </div>
            `;
        }
    );
}