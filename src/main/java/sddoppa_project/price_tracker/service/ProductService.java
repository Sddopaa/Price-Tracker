package sddoppa_project.price_tracker.service;

// Импорт сущностей Product и PriceHistory
import sddoppa_project.price_tracker.entity.Product;
import sddoppa_project.price_tracker.entity.PriceHistory;

// Импорт репозиториев
import sddoppa_project.price_tracker.repository.ProductRepository;
import sddoppa_project.price_tracker.repository.PriceHistoryRepository;

// Spring-аннотации
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException; // Исключение на случай ошибок парсинга
import java.math.BigDecimal; // Тип для цены
import java.time.LocalDateTime; // Тип для времени
import java.util.Optional; // Обёртка, чтобы не было null

@Service // Помечаем класс как сервис Spring (bean)
public class ProductService {

    @Autowired // Внедряем ProductRepository (автоматически)
    private ProductRepository productRepository;

    @Autowired // Внедряем PriceHistoryRepository
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired // Внедряем ParserService (наш парсер)
    private ParserService parserService;

    /**
     * Добавляет новый товар для отслеживания
     * @param url Ссылка на товар
     * @return Сохраненный товар
     * @throws IOException Если не удалось загрузить страницу
     */
    @Transactional // Все операции в методе выполняются в одной транзакции
    public Product addProduct(String url) throws IOException {

        // Ищем товар по URL в базе (если уже есть)
        Product existingProduct = productRepository.findByUrl(url);

        // Если товар уже есть
        if (existingProduct != null) {
            // Обновляем его цену (парсим сайт и сохраняем новую цену)
            updateProductPrice(existingProduct);

            // Возвращаем существующий товар (уже обновлённый)
            return existingProduct;
        }

        // Если товара нет в базе — парсим его с сайта
        Product parsedProduct = parserService.parseProduct(url);

        // Сохраняем распарсенный товар в базе
        Product savedProduct = productRepository.save(parsedProduct);

        // Сохраняем текущую цену товара в историю
        savePriceToHistory(savedProduct);

        // Возвращаем сохранённый товар
        return savedProduct;
    }

    /**
     * Обновляет цену существующего товара
     * @param productId ID товара
     * @return true если цена изменилась
     */
    @Transactional // Тоже в транзакции
    public boolean updateProductPrice(Long productId) {

        // Ищем товар по ID
        Optional<Product> productOpt = productRepository.findById(productId);

        // Если товара нет — возвращаем false
        if (productOpt.isEmpty()) return false;

        // Если товар есть — обновляем цену (внутренний метод)
        return updateProductPrice(productOpt.get());
    }

    /**
     * Обновляет цену товара (внутренний метод)
     */
    private boolean updateProductPrice(Product product) {
        try {
            // Сохраняем старую цену (до обновления)
            BigDecimal oldPrice = product.getCurrentPrice();

            // Парсим актуальную цену с сайта
            Product parsedProduct = parserService.parseProduct(product.getUrl());

            // Берём новую цену из распарсенного товара
            BigDecimal newPrice = parsedProduct.getCurrentPrice();

            // Если цена не изменилась или новая цена null — ничего не делаем
            if (newPrice == null || newPrice.equals(oldPrice)) {
                return false;
            }

            // Устанавливаем новую цену в товар
            product.setCurrentPrice(newPrice);

            // Обновляем дату последней проверки
            product.setLastChecked(LocalDateTime.now());

            // Обновляем имя товара на случай изменения названия
            product.setName(parsedProduct.getName());

            // Сохраняем обновлённый товар в базе
            productRepository.save(product);

            // Сохраняем новую цену в историю
            savePriceToHistory(product);

            // Возвращаем true — цена изменилась
            return true;

        } catch (IOException e) { // Если парсер не смог загрузить страницу
            // Печатаем ошибку в консоль
            System.err.println("Не удалось обновить цену для товара ID=" + product.getId() +
                    ", URL=" + product.getUrl() + ", ошибка: " + e.getMessage());

            // Возвращаем false — цена не обновилась
            return false;
        }
    }

    /**
     * Сохраняет текущую цену товара в историю
     */
    private void savePriceToHistory(Product product) {

        // Если цена не задана — не сохраняем
        if (product.getCurrentPrice() == null) return;

        // Создаём объект истории цены
        PriceHistory priceHistory = new PriceHistory();

        // Привязываем историю к товару (важно!)
        priceHistory.setProduct(product);

        // Устанавливаем цену
        priceHistory.setPrice(product.getCurrentPrice());

        // Время checkedAt установится автоматически в @PrePersist

        // Сохраняем запись истории в базе
        priceHistoryRepository.save(priceHistory);
    }

    /**
     * Получает товар по ID
     */
    public Optional<Product> getProductById(Long id) {
        // Ищем товар в репозитории
        return productRepository.findById(id);
    }

    /**
     * Получает все товары
     */
    public java.util.List<Product> getAllProducts() {
        // Возвращаем список всех товаров
        return productRepository.findAll();
    }

    /**
     * Получает историю цен для товара
     */
    public java.util.List<PriceHistory> getPriceHistory(Long productId) {
        // Получаем список истории по ID товара
        return priceHistoryRepository.findByProductIdOrderByCheckedAtDesc(productId);
    }

    /**
     * Получает последнюю цену из истории
     */
    public Optional<PriceHistory> getLatestPrice(Long productId) {
        // Получаем последнюю запись из истории
        return Optional.ofNullable(
                priceHistoryRepository.findFirstByProductIdOrderByCheckedAtDesc(productId)
        );
    }

    /**
     * Удаляет товар
     */
    public boolean deleteProduct(Long productId) {
        // Проверяем, существует ли товар
        if (productRepository.existsById(productId)) {

            // Удаляем товар по ID
            productRepository.deleteById(productId);

            // Возвращаем true — удалено
            return true;
        }

        // Если товара нет — возвращаем false
        return false;
    }
}
