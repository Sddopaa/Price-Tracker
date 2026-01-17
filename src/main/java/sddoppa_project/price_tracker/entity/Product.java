package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String url;  // Ссылка на товар (DNS/MVideo)

    @Column(name = "name", length = 500)
    private String name;  // Название товара

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;  // Текущая цена

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;  // Когда последний раз проверяли цену

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // Когда добавили товар

    @Enumerated(EnumType.STRING)
    @Column(name = "store_type", nullable = false)
    private StoreType storeType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Enum для типов магазинов
    public enum StoreType {
        DNS,
        MVIDEO,
        CITILINK,
        WILDBERRIES,
        OZON,
        YANDEX_MARKET,
        OTHER
    }
}