package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity // JPA-сущность
@Table(name = "user_products") // Имя таблицы
public class UserProduct {

    @Id // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Многие отслеживания → один пользователь
    @JoinColumn(name = "user_id", nullable = false) // FK на users.id
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Многие отслеживания → один товар
    @JoinColumn(name = "product_id", nullable = false) // FK на products.id
    private Product product;

    @Column(name = "target_price", precision = 10, scale = 2) // Целевая цена
    private BigDecimal targetPrice;

    @Column(name = "notification_sent", nullable = false) // Уведомление отправлено?
    private Boolean notificationSent = false;

    @Column(name = "created_at", nullable = false) // Когда начали отслеживать
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Устанавливаем дату начала отслеживания
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public Boolean getNotificationSent() { return notificationSent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setProduct(Product product) { this.product = product; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }
    public void setNotificationSent(Boolean notificationSent) { this.notificationSent = notificationSent; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
