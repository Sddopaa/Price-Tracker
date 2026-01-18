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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url", unique = true, nullable = false, length = 500)
    private String url;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_type", nullable = false, length = 20)
    private StoreType storeType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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