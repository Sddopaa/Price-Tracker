package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_products")
public class UserProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID пользователя, который отслеживает

    @Column(name = "product_id", nullable = false)
    private Long productId; // ID товара, который отслеживается

    @Column(name = "target_price", precision = 10, scale = 2)
    private BigDecimal targetPrice; // Целевая цена для уведомления

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false; // Отправлено ли уже уведомление

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Когда начали отслеживать

    // ГЕТТЕРЫ
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public Boolean getNotificationSent() { return notificationSent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // СЕТТЕРЫ
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public void setNotificationSent(Boolean notificationSent) { this.notificationSent = notificationSent; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Устанавливаем дату начала отслеживания
    }
}