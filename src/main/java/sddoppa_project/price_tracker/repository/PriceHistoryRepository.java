package sddoppa_project.price_tracker.repository;

import sddoppa_project.price_tracker.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByProductIdOrderByCheckedAtDesc(Long productId);
    PriceHistory findFirstByProductIdOrderByCheckedAtDesc(Long productId);
}