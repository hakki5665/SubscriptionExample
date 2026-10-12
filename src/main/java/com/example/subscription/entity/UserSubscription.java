package com.example.subscription.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "subscription_type", nullable = false)
    private String subscriptionType;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}


