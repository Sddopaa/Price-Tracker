package sddoppa_project.price_tracker.service;

import sddoppa_project.price_tracker.entity.Product;
import sddoppa_project.price_tracker.entity.PriceHistory;
import sddoppa_project.price_tracker.repository.ProductRepository;
import sddoppa_project.price_tracker.repository.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository; // Репозиторий для работы с товарами

    @Autowired
    private PriceHistoryRepository priceHistoryRepository; // Репозиторий для истории цен

    @Autowired
    private ParserService parserService; // Наш парсер для загрузки цен

    /**
     * Добавляет новый товар для отслеживания
     * @param url Ссылка на товар
     * @return Сохраненный товар
     * @throws IOException Если не удалось загрузить страницу
     */
    @Transactional // Все операции в одной транзакции
    public Product addProduct(String url) throws IOException {
        // 1. Проверяем, может товар уже есть в базе
        Product existingProduct = productRepository.findByUrl(url);
        if (existingProduct != null) {
            // Товар уже есть - просто обновляем цену
            updateProductPrice(existingProduct);
            return existingProduct;
        }

        // 2. Товара нет - парсим его с сайта
        Product parsedProduct = parserService.parseProduct(url);

        // 3. Сохраняем товар в базу данных
        Product savedProduct = productRepository.save(parsedProduct);

        // 4. Сохраняем текущую цену в историю
        savePriceToHistory(savedProduct);

        return savedProduct;
    }

    /**
     * Обновляет цену существующего товара
     * @param productId ID товара
     * @return true если цена изменилась
     */
    @Transactional
    public boolean updateProductPrice(Long productId) {
        // 1. Находим товар в базе
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false; // Товар не найден
        }

        Product product = productOpt.get();
        return updateProductPrice(product);
    }

    /**
     * Обновляет цену товара (внутренний метод)
     */
    private boolean updateProductPrice(Product product) {
        try {
            // 1. Запоминаем старую цену
            BigDecimal oldPrice = product.getCurrentPrice();

            // 2. Парсим актуальную цену с сайта
            Product parsedProduct = parserService.parseProduct(product.getUrl());
            BigDecimal newPrice = parsedProduct.getCurrentPrice();

            // 3. Если цена не изменилась - ничего не делаем
            if (newPrice == null || newPrice.equals(oldPrice)) {
                return false;
            }

            // 4. Обновляем товар
            product.setCurrentPrice(newPrice);
            product.setLastChecked(LocalDateTime.now());
            product.setName(parsedProduct.getName()); // На случай если изменилось название

            productRepository.save(product);

            // 5. Сохраняем новую цену в историю
            savePriceToHistory(product);

            return true; // Цена изменилась

        } catch (IOException e) {
            System.err.println("Не удалось обновить цену для товара ID=" + product.getId() +
                    ", URL=" + product.getUrl() + ", ошибка: " + e.getMessage());
            return false;
        }
    }

    /**
     * Сохраняет текущую цену товара в историю
     */
    private void savePriceToHistory(Product product) {
        if (product.getCurrentPrice() == null) {
            return; // Если цены нет - не сохраняем
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProductId(product.getId());
        priceHistory.setPrice(product.getCurrentPrice());
        // checkedAt установится автоматически в @PrePersist

        priceHistoryRepository.save(priceHistory);
    }

    /**
     * Получает товар по ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Получает все товары
     */
    public java.util.List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Получает историю цен для товара
     */
    public java.util.List<PriceHistory> getPriceHistory(Long productId) {
        return priceHistoryRepository.findByProductIdOrderByCheckedAtDesc(productId);
    }

    /**
     * Получает последнюю цену из истории
     */
    public Optional<PriceHistory> getLatestPrice(Long productId) {
        return Optional.ofNullable(
                priceHistoryRepository.findFirstByProductIdOrderByCheckedAtDesc(productId)
        );
    }

    /**
     * Удаляет товар
     */
    public boolean deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }
}