package sddoppa_project.price_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sddoppa_project.price_tracker.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET: http://localhost:8080/api/products/add?url=https://www.dns-shop.ru/...
    @GetMapping("/add")
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

    // GET: http://localhost:8080/api/products/1
    @GetMapping("/{id}")
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

    // GET: http://localhost:8080/api/products/all
    @GetMapping("/all")
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