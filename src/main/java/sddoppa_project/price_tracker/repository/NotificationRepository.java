package sddoppa_project.price_tracker.repository;

import sddoppa_project.price_tracker.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByProductId(Long productId);
    List<Notification> findByStatus(String status);
}