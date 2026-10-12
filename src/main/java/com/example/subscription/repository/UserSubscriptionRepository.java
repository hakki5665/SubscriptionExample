package com.example.subscription.repository;

import com.example.subscription.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, String> {

    @Query("SELECT s FROM UserSubscription s WHERE s.subscriptionType = 'PREMIUM' AND s.expiresAt < :now")
    List<UserSubscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);
}