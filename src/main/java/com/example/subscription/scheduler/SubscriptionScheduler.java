package com.example.subscription.scheduler;

import com.example.subscription.entity.UserSubscription;
import com.example.subscription.repository.UserSubscriptionRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionScheduler {

    private final UserSubscriptionRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SubscriptionScheduler(UserSubscriptionRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "checkExpiredSubscriptionsLock", lockAtLeastFor = "30s", lockAtMostFor = "5m")
    @Transactional
    public void checkExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<UserSubscription> expiredList = repository.findExpiredSubscriptions(now);

        if (expiredList.isEmpty()) {
            return;
        }

        for (UserSubscription sub : expiredList) {
            sub.setSubscriptionType("FREE");
            repository.save(sub);

            kafkaTemplate.send("subscription-invalidations-topic", sub.getUsername(), sub.getUsername());
            System.out.println("Подписка пользователя " + sub.getUsername() + " истекла. Отправлено событие в Kafka.");
        }
    }
}