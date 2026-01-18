package sddoppa_project.price_tracker.repository;

import sddoppa_project.price_tracker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByUrl(String url);
    List<Product> findByStoreType(Product.StoreType storeType);
}