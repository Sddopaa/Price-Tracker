package sddoppa_project.price_tracker.repository;

import sddoppa_project.price_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByTelegramChatId(String telegramChatId);
}