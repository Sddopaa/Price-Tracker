package sddoppa_project.price_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sddoppa_project.price_tracker.service.ProductService;
import sddoppa_project.price_tracker.service.ParserService;
import sddoppa_project.price_tracker.entity.Product;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ParserService parserService;

    // ===========================
    // 1) TEST endpoints
    // ===========================
    @GetMapping("/test")
    public String test() {
        return "Application is running. Time: " + java.time.LocalDateTime.now();
    }

    @GetMapping("/test-parse")
    public String testParse(@RequestParam(required = false) String url) {
        if (url == null || url.isEmpty()) {
            return "Please provide url parameter: /api/test-parse?url=https://www.dns-shop.ru/...";
        }
        return "Will parse: " + url;
    }

    // ===========================
    // 2) ПРЯМОЙ ТЕСТ ПАРСЕРА (без сохранения в БД)
    // ===========================
    @GetMapping("/test-parser")
    public String testParserDirectly(@RequestParam String url) {
        try {
            // Парсим товар напрямую через ParserService
            Product product = parserService.parseProduct(url);

            StringBuilder result = new StringBuilder();
            result.append("=== ТЕСТ ПАРСЕРА ===\n");
            result.append("URL: ").append(url).append("\n");
            result.append("Магазин: ").append(product.getStoreType()).append("\n");

            // Проверяем название
            if (product.getName() != null && !product.getName().isEmpty()) {
                result.append("Название: ").append(product.getName()).append("\n");
            } else {
                result.append("Название: НЕ НАЙДЕНО\n");
            }

            // Проверяем цену
            if (product.getCurrentPrice() != null) {
                result.append("Цена: ").append(product.getCurrentPrice()).append(" RUB\n");
            } else {
                result.append("Цена: НЕ НАЙДЕНА\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Ошибка парсинга: " + e.getMessage();
        }
    }

    // ===========================
    // 3) PRODUCT endpoints
    // ===========================
    // Добавить товар в базу данных
    // GET: http://localhost:8080/api/products/add?url=https://www.dns-shop.ru/...
    @GetMapping("/products/add")
    public String addProduct(@RequestParam String url) {
        try {
            // Добавляем товар через ProductService (сохраняет в БД)
            Product product = productService.addProduct(url);

            StringBuilder result = new StringBuilder();
            result.append("✅ Товар добавлен:\n");
            result.append("ID: ").append(product.getId()).append("\n");
            result.append("Название: ").append(product.getName()).append("\n");
            result.append("Цена: ").append(product.getCurrentPrice()).append(" RUB\n");
            result.append("Магазин: ").append(product.getStoreType()).append("\n");
            result.append("URL: ").append(product.getUrl());

            return result.toString();

        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }

    // Получить товар по ID
    // GET: http://localhost:8080/api/products/1
    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable Long id) {
        var product = productService.getProductById(id);
        if (product.isPresent()) {
            Product p = product.get();

            StringBuilder result = new StringBuilder();
            result.append("📦 Товар #").append(id).append("\n");
            result.append("Название: ").append(p.getName()).append("\n");
            result.append("Цена: ").append(p.getCurrentPrice()).append(" RUB\n");
            result.append("Магазин: ").append(p.getStoreType()).append("\n");
            result.append("URL: ").append(p.getUrl());

            return result.toString();
        }
        return "Товар не найден";
    }

    // Получить все товары
    // GET: http://localhost:8080/api/products/all
    @GetMapping("/products/all")
    public String getAllProducts() {
        var products = productService.getAllProducts();
        if (products.isEmpty()) {
            return "В базе нет товаров";
        }

        StringBuilder result = new StringBuilder();
        result.append("Все товары (").append(products.size()).append("):\n\n");

        for (var product : products) {
            result.append("#").append(product.getId())
                    .append(": ").append(product.getName())
                    .append(" - ").append(product.getCurrentPrice()).append(" RUB\n");
        }

        return result.toString();
    }

    // ===========================
    // 4) TEST ЦЕНЫ (обновление цены)
    // ===========================
    @GetMapping("/products/{id}/update-price")
    public String updateProductPrice(@PathVariable Long id) {
        try {
            // Обновляем цену товара
            boolean updated = productService.updateProductPrice(id);

            // Получаем товар после обновления
            var productOpt = productService.getProductById(id);

            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                if (updated) {
                    return "✅ Цена обновлена:\n" +
                            "Товар: " + product.getName() + "\n" +
                            "Цена: " + product.getCurrentPrice() + " RUB";
                } else {
                    return "ℹ️ Цена не изменилась:\n" +
                            "Товар: " + product.getName() + "\n" +
                            "Цена: " + product.getCurrentPrice() + " RUB";
                }
            }

            return "❌ Товар не найден";

        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }
}
//package sddoppa_project.price_tracker.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import sddoppa_project.price_tracker.service.ProductService;
//
//@RestController
//@RequestMapping("/api")
//public class MainController {
//
//    @Autowired
//    private ProductService productService;
//
//    // ===========================
//    // 1) TEST endpoints
//    // ===========================
//    @GetMapping("/test")
//    public String test() {
//        return "Application is running. Time: " + java.time.LocalDateTime.now();
//    }
//
//    @GetMapping("/test-parse")
//    public String testParse(@RequestParam(required = false) String url) {
//        if (url == null || url.isEmpty()) {
//            return "Please provide url parameter: /api/test-parse?url=https://www.dns-shop.ru/...";
//        }
//        return "Will parse: " + url;
//    }
//
//    // ===========================
//    // 2) PRODUCT endpoints
//    // ===========================
//    // Добавить товар
//    // GET: http://localhost:8080/api/products/add?url=https://www.dns-shop.ru/...
//    @GetMapping("/products/add")
//    public String addProduct(@RequestParam String url) {
//        try {
//            var product = productService.addProduct(url);
//            return "Product added: " + product.getName() +
//                    ", Price: " + product.getCurrentPrice() +
//                    ", Store: " + product.getStoreType();
//        } catch (Exception e) {
//            return "Error: " + e.getMessage();
//        }
//    }
//
//    // Получить товар по ID
//    // GET: http://localhost:8080/api/products/1
//    @GetMapping("/products/{id}")
//    public String getProduct(@PathVariable Long id) {
//        var product = productService.getProductById(id);
//        if (product.isPresent()) {
//            var p = product.get();
//            return "Product #" + id +
//                    "\nName: " + p.getName() +
//                    "\nPrice: " + p.getCurrentPrice() +
//                    "\nURL: " + p.getUrl();
//        }
//        return "Product not found";
//    }
//
//    // Получить все товары
//    // GET: http://localhost:8080/api/products/all
//    @GetMapping("/products/all")
//    public String getAllProducts() {
//        var products = productService.getAllProducts();
//        if (products.isEmpty()) {
//            return "No products in database";
//        }
//
//        StringBuilder result = new StringBuilder("All products (" + products.size() + "):\n");
//        for (var product : products) {
//            result.append("#").append(product.getId())
//                    .append(": ").append(product.getName())
//                    .append(" - ").append(product.getCurrentPrice()).append(" RUB\n");
//        }
//        return result.toString();
//    }
//}
