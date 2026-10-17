package com.example.subscription.scheduler;

import com.example.subscription.BaseSubscriptionIntegrationTest;
import com.example.subscription.entity.UserSubscription;
import com.example.subscription.entity.SubscriptionType;
import com.example.subscription.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@DisplayName("Subscription Lifecycle & Kafka Events Tests")
class SubscriptionSchedulerIntegrationTest extends BaseSubscriptionIntegrationTest {

    @Autowired
    private UserSubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionScheduler subscriptionScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Expired subscription detected -> drop level to FREE and emit Kafka event")
    void shouldProcessExpiredSubscriptionAndEmitKafkaEvent() throws Exception {
        String username = "expired_customer_login";

        subscriptionRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM shedlock");

        UserSubscription expiredSub = new UserSubscription();
        expiredSub.setUsername(username);
        expiredSub.setSubscriptionType(SubscriptionType.PREMIUM);
        expiredSub.setExpiresAt(LocalDateTime.of(2000, 1, 1, 0, 0));
        subscriptionRepository.save(expiredSub);

        subscriptionScheduler.checkExpiredSubscriptions();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<UserSubscription> updatedSubOpt = subscriptionRepository.findById(username);
            assertThat(updatedSubOpt).isPresent();
            assertThat(updatedSubOpt.get().getSubscriptionType()).isEqualTo(SubscriptionType.FREE);
        });

        verify(kafkaTemplate).send(anyString(), anyString(), anyString());
    }
}