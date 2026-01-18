package sddoppa_project.price_tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email; // Электронная почта для входа

    @Column(name = "password", nullable = false, length = 100)
    private String password; // Хэшированный пароль

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username; // Имя пользователя для отображения

    @Column(name = "telegram_chat_id", length = 50)
    private String telegramChatId; // ID чата в Telegram для уведомлений

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER"; // Роль: USER, ADMIN

    @Column(name = "last_login")
    private LocalDateTime lastLogin; // Когда последний раз заходил

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Дата регистрации

    // ГЕТТЕРЫ
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public String getTelegramChatId() { return telegramChatId; }
    public String getRole() { return role; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // СЕТТЕРЫ
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUsername(String username) { this.username = username; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
    public void setRole(String role) { this.role = role; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Устанавливаем дату регистрации
    }
}