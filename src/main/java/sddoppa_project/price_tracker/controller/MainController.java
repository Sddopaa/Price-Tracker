package sddoppa_project.price_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sddoppa_project.price_tracker.service.ProductService;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private ProductService productService;

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
    // 2) PRODUCT endpoints
    // ===========================
    // Добавить товар
    // GET: http://localhost:8080/api/products/add?url=https://www.dns-shop.ru/...
    @GetMapping("/products/add")
    public String addProduct(@RequestParam String url) {
        try {
            var product = productService.addProduct(url);
            return "Product added: " + product.getName() +
                    ", Price: " + product.getCurrentPrice() +
                    ", Store: " + product.getStoreType();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Получить товар по ID
    // GET: http://localhost:8080/api/products/1
    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable Long id) {
        var product = productService.getProductById(id);
        if (product.isPresent()) {
            var p = product.get();
            return "Product #" + id +
                    "\nName: " + p.getName() +
                    "\nPrice: " + p.getCurrentPrice() +
                    "\nURL: " + p.getUrl();
        }
        return "Product not found";
    }

    // Получить все товары
    // GET: http://localhost:8080/api/products/all
    @GetMapping("/products/all")
    public String getAllProducts() {
        var products = productService.getAllProducts();
        if (products.isEmpty()) {
            return "No products in database";
        }

        StringBuilder result = new StringBuilder("All products (" + products.size() + "):\n");
        for (var product : products) {
            result.append("#").append(product.getId())
                    .append(": ").append(product.getName())
                    .append(" - ").append(product.getCurrentPrice()).append(" RUB\n");
        }
        return result.toString();
    }
}
