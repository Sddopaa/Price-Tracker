package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент в БД
    private Long id;

    @Column(name = "url", unique = true, nullable = false, length = 500)
    private String url; // Ссылка на товар

    @Column(name = "name", length = 500)
    private String name; // Название товара

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice; // Текущая цена

    @Column(name = "last_checked")
    private LocalDateTime lastChecked; // Когда последний раз проверяли цену

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Когда добавили в систему

    @Enumerated(EnumType.STRING) // Храним как текст "DNS", "MVIDEO"
    @Column(name = "store_type", nullable = false, length = 20)
    private StoreType storeType; // Магазин: DNS, MVideo и т.д.

    // ГЕТТЕРЫ
    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getName() { return name; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public LocalDateTime getLastChecked() { return lastChecked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public StoreType getStoreType() { return storeType; }

    // СЕТТЕРЫ
    public void setId(Long id) { this.id = id; }
    public void setUrl(String url) { this.url = url; }
    public void setName(String name) { this.name = name; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStoreType(StoreType storeType) { this.storeType = storeType; }

    @PrePersist // Выполняется перед сохранением в БД
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Устанавливаем дату создания
    }

    // Перечисление магазинов
    public enum StoreType {
        DNS,           // dns-shop.ru
        MVIDEO,        // mvideo.ru
        CITILINK,      // citilink.ru
        WILDBERRIES,   // wildberries.ru
        OZON,          // ozon.ru
        YANDEX_MARKET, // market.yandex.ru
        OTHER          // Другие магазины
    }
}