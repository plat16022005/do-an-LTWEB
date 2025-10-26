/**
 * File: notification.js
 * Chức năng: Tự động đếm thông báo chưa đọc (dùng Polling).
 * KHÔNG phụ thuộc vào WebSocket.
 */

// Tần suất hỏi server (10000ms = 10 giây)
const POLLING_INTERVAL = 10000; 

/**
 * Hàm này gọi API backend để lấy số lượng thông báo chưa đọc.
 */
async function fetchUnreadNotificationCount() {
    // Thêm console.log ở đây để debug
    console.log("Đang gọi API /notification/unread-count...");
    
    try {
        // 1. Gọi API (API này phải được tạo ở Backend)
        // Thêm cache-buster để tránh trình duyệt lưu kết quả cũ
        const response = await fetch('/notification/unread-count?t=' + new Date().getTime());
        
        // 2. Nếu server lỗi, không làm gì cả
        if (!response.ok) {
            console.error("Lỗi API đếm thông báo:", response.status);
            return;
        }
        
        // 3. Lấy dữ liệu JSON, ví dụ: { "count": 5 }
        const data = await response.json(); 
        console.log("Nhận được số lượng:", data.count);
        
        // 4. Cập nhật UI với số lượng mới
        updateNotificationBadge(data.count);
        
    } catch (error) {
        // Lỗi này xảy ra nếu mất mạng, hoặc server sập
        console.error('Lỗi khi tải số lượng thông báo:', error);
    }
}

/**
 * Hàm này dùng để cập nhật giao diện (UI) của huy hiệu thông báo.
 * @param {number} count - Số lượng thông báo chưa đọc.
 */
function updateNotificationBadge(count) {
    const notificationBadge = document.getElementById('notification-badge');
    
    // Thoát nếu trang này không có huy hiệu (ví dụ: trang login)
    if (!notificationBadge) {
        return; 
    }

    if (count > 0) {
        notificationBadge.textContent = count > 99 ? '99+' : count;
        notificationBadge.classList.remove('d-none');
    } else {
        notificationBadge.classList.add('d-none');
    }
}

/**
 * Hàm khởi chạy
 * Chờ cho toàn bộ trang HTML tải xong, sau đó bắt đầu
 */
async function fetchUnreadMessageCount() {
    try {
        // 1. Gọi API mới (chúng ta sẽ tạo ở Bước 2)
        const response = await fetch('/message/unread-count?t=' + new Date().getTime());
        
        if (!response.ok) {
             // console.error("Lỗi API đếm tin nhắn:", response.status);
            return;
        }
        
        // 3. Lấy dữ liệu JSON, ví dụ: { "count": 3 }
        const data = await response.json(); 
        
        // 4. Cập nhật UI cho CÁI PHONG BÌ ✉️
        updateMessageBadge(data.count);
        
    } catch (error) {
        // console.error('Lỗi khi tải số lượng tin nhắn:', error);
    }
}

/**
 * Hàm này dùng để cập nhật giao diện (UI) của CHẤM ĐỎ tin nhắn.
 * @param {number} count - Số lượng tin nhắn/cuộc trò chuyện chưa đọc.
 */
function updateMessageBadge(count) {
    // 1. Tìm chấm đỏ (ID này phải khớp với HTML bạn đã sửa)
    const messageBadge = document.getElementById('message-sidebar-badge'); // ✉️
    if (!messageBadge) return; 

    // 2. Cập nhật UI
    if (count > 0) {
        // Nếu có tin nhắn, chỉ cần làm hiện chấm đỏ (không cần số)
        messageBadge.classList.remove('d-none');
    } else {
        // Nếu không có, ẩn đi
        messageBadge.classList.add('d-none');
    }
}
document.addEventListener('DOMContentLoaded', () => {
    
    console.log("Bắt đầu khởi chạy hệ thống đếm thông báo (Polling)...");
    
    // 1. Lấy số lượng ngay lập tức khi vừa tải trang
    fetchUnreadNotificationCount();
	fetchUnreadMessageCount();
    // 2. Thiết lập "Polling" (Hỏi liên tục)
    // Cứ mỗi 10 giây, gọi lại hàm fetchUnreadNotificationCount
	setInterval(() => {
	        fetchUnreadNotificationCount();
	        fetchUnreadMessageCount();
	    }, POLLING_INTERVAL);
});