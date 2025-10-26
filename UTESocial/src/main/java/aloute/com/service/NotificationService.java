package aloute.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.repository.NotificationRepository;

@Service
public class NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;
	@Transactional(readOnly = true) // Dùng readOnly để tối ưu
	public long getUnreadCount(Integer userId) {
	    // (Chúng ta sẽ tạo hàm này ở bước 2c)
		return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
	}
}
