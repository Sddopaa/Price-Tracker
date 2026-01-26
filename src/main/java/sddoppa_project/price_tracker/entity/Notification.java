package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Класс является JPA-сущностью (таблица БД)
@Table(name = "notifications") // Явное имя таблицы
public class Notification {

    @Id // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Один пользователь может иметь много уведомлений
    @JoinColumn(name = "user_id", nullable = false) // FK на users.id
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Один товар может иметь много уведомлений
    @JoinColumn(name = "product_id", nullable = false) // FK на products.id
    private Product product;

    @Enumerated(EnumType.STRING) // Enum хранится в БД как строка
    @Column(nullable = false, length = 50) // Тип уведомления
    private NotificationType type;

    @Column(length = 1000) // Текст уведомления
    private String message;

    @Enumerated(EnumType.STRING) // Enum хранится в БД как строка
    @Column(nullable = false, length = 20) // Статус отправки
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "created_at", nullable = false) // Когда создали уведомление
    private LocalDateTime createdAt;

    @PrePersist // Перед сохранением
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Автоматически ставим дату создания
    }

    public enum NotificationType {
        TARGET_PRICE, // Достигнута целевая цена
        PRICE_DROP,   // Цена упала
        PRICE_RISE    // Цена выросла
    }

    public enum NotificationStatus {
        PENDING, // В очереди
        SENT,    // Отправлено
        FAILED   // Ошибка отправки
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setProduct(Product product) { this.product = product; }
    public void setType(NotificationType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
