package sddoppa_project.price_tracker.repository;

import sddoppa_project.price_tracker.entity.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    List<UserProduct> findByUserId(Long userId);
    List<UserProduct> findByProductId(Long productId);
    UserProduct findByUserIdAndProductId(Long userId, Long productId);
}