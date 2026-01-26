package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity // Класс является JPA-сущностью (таблица БД)
@Table(name = "price_history") // Явное имя таблицы
public class PriceHistory {

    @Id // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Много записей истории → один товар
    @JoinColumn(name = "product_id", nullable = false) // FK на products.id
    private Product product;

    @Column(precision = 10, scale = 2, nullable = false) // Цена в момент проверки
    private BigDecimal price;

    @Column(name = "checked_at", nullable = false) // Время проверки цены
    private LocalDateTime checkedAt;

    @PrePersist // Перед сохранением
    protected void onCreate() {
        this.checkedAt = LocalDateTime.now(); // Автоматически ставим время проверки
    }

    // Getters
    public Long getId() {
        return id;
    }
    public Product getProduct() {
        return product;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
