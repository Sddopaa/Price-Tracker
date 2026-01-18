package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId; // ID товара (связь с таблицей products)

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price; // Цена в момент проверки

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt; // Время проверки цены

    // ГЕТТЕРЫ
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getCheckedAt() { return checkedAt; }

    // СЕТТЕРЫ
    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }

    @PrePersist
    protected void onCreate() {
        checkedAt = LocalDateTime.now(); // Устанавливаем текущее время проверки
    }
}