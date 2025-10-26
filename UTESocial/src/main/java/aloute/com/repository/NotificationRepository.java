package aloute.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aloute.com.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
    long countByUser_UserIdAndIsReadFalse(Integer userId);
}
