package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_telegram", columnList = "telegram_chat_id"),
        @Index(name = "idx_user_username", columnList = "username")
})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "username", unique = true, nullable = false, length = 50)
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