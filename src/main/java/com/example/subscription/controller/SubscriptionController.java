package com.example.subscription.controller;

import com.example.subscription.entity.SubscriptionType;
import com.example.subscription.repository.UserSubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final UserSubscriptionRepository repository;

    public SubscriptionController(UserSubscriptionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable("username") String username) {
        return repository.findById(username)
                .map(sub -> ResponseEntity.ok(new SubscriptionResponse(sub.getUsername(), sub.getSubscriptionType().name())))
                .orElseGet(() -> ResponseEntity.ok(new SubscriptionResponse(username, SubscriptionType.FREE.name())));
    }
}

