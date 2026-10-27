package com.adent.identity.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    @Column(unique = true, length = 30)
    private String phone;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(name = "platform_admin", nullable = false)
    private boolean platformAdmin;
    @Column(nullable = false)
    private boolean active = true;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public boolean isPlatformAdmin() { return platformAdmin; }
    public void setPlatformAdmin(boolean platformAdmin) { this.platformAdmin = platformAdmin; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
