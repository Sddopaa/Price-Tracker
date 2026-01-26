package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Класс является JPA-сущностью (таблица БД)
@Table(name = "users") // Явное имя таблицы в БД
public class User {

    @Id // Первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // Email для входа
    private String email;

    @Column(nullable = false, length = 100) // Хэш пароля
    private String password;

    @Column(nullable = false, unique = true, length = 50) // Имя пользователя
    private String username;

    @Column(name = "telegram_chat_id", length = 50) // Telegram ID для уведомлений
    private String telegramChatId;

    @Enumerated(EnumType.STRING) // Роль хранится как текст
    @Column(nullable = false, length = 20) // Роль: USER, ADMIN
    private Role role = Role.USER;

    @Column(name = "last_login") // Время последнего входа
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false) // Дата регистрации
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Автоматически ставим дату регистрации
    }

    public enum Role {
        USER,
        ADMIN
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public String getTelegramChatId() { return telegramChatId; }
    public Role getRole() { return role; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt;}

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUsername(String username) { this.username = username; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
    public void setRole(Role role) { this.role = role; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
