package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Кому отправили

    @Column(name = "product_id", nullable = false)
    private Long productId; // По какому товару

    @Column(name = "type", nullable = false, length = 50)
    private String type; // Тип: "TARGET_PRICE", "PRICE_DROP", "PRICE_RISE"

    @Column(name = "message", length = 1000)
    private String message; // Текст уведомления

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt; // Когда отправили

    @Column(name = "status", nullable = false, length = 20)
    private String status = "SENT"; // Статус: "SENT", "FAILED", "PENDING"

    // ГЕТТЕРЫ
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getSentAt() { return sentAt; }
    public String getStatus() { return status; }

    // СЕТТЕРЫ
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public void setStatus(String status) { this.status = status; }

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now(); // Устанавливаем время отправки
    }
}