package com.example.subscription.controller;


public class SubscriptionResponse {
    private String username;
    private String subscriptionType;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(String username, String subscriptionType) {
        this.username = username;
        this.subscriptionType = subscriptionType;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
}