package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Класс является JPA-сущностью (таблица БД)
@Table(name = "products") // Явное имя таблицы в БД
public class Product {

    @Id // Первичный ключ таблицы
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент (генерируется БД)
    private Long id;

    @Column(nullable = false, unique = true, length = 500) // URL товара (уникальный)
    private String url;

    @Column(length = 500) // Название товара
    private String name;

    @Column(name = "current_price", precision = 10, scale = 2) // Текущая цена товара
    private BigDecimal currentPrice;

    @Column(name = "last_checked") // Дата последней проверки цены
    private LocalDateTime lastChecked;

    @Column(name = "created_at", nullable = false) // Дата добавления товара в систему
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // Enum хранится в БД как строка
    @Column(name = "store_type", nullable = false, length = 30) // Тип магазина
    private StoreType storeType;

    @OneToMany(
            mappedBy = "product", // Связь описана в PriceHistory.product
            cascade = CascadeType.ALL, // История цен удаляется вместе с товаром
            orphanRemoval = true, // Удаляем записи истории без родителя
            fetch = FetchType.LAZY // История загружается только при обращении
    )
    private List<PriceHistory> priceHistory = new ArrayList<>();

    @PrePersist // Вызывается перед первым сохранением сущности в БД
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Автоматически ставим дату создания
    }

    // Поддерживаемые магазины
    public enum StoreType {
        DNS,           // dns-shop.ru
        MVIDEO,        // mvideo.ru
        CITILINK,      // citilink.ru
        WILDBERRIES,   // wildberries.ru
        OZON,          // ozon.ru
        YANDEX_MARKET, // market.yandex.ru
        OTHER          // Любые другие магазины
    }

    // Getters
    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getName() { return name; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public LocalDateTime getLastChecked() { return lastChecked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public StoreType getStoreType() { return storeType; }
    public List<PriceHistory> getPriceHistory() { return priceHistory; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUrl(String url) { this.url = url; }
    public void setName(String name) { this.name = name; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStoreType(StoreType storeType) { this.storeType = storeType; }
    public void setPriceHistory(List<PriceHistory> priceHistory) { this.priceHistory = priceHistory; }
}
