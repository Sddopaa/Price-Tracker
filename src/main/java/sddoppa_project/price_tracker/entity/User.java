package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),               // Для поиска по email
    @Index(name = "idx_user_telegram", columnList = "telegram_chat_id"), // Для поиска по Telegram
    @Index(name = "idx_user_username", columnList = "username")          // Для поиска по username
})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Неверный формат email")
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    @Size(min = 3, message = "Пароль должен быть минимум 3 символа")
    private String password;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @Size(min = 3, message = "Имя пользователя должно быть минимум 3 символа")
    private String username;

    @Column(name = "telegram_chat_id", length = 50)
    private String telegramChatId;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}