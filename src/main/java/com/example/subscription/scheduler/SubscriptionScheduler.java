package com.example.subscription.scheduler;

import com.example.subscription.entity.UserSubscription;
import com.example.subscription.entity.SubscriptionType;
import com.example.subscription.repository.UserSubscriptionRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionScheduler.class);

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
            sub.setSubscriptionType(SubscriptionType.FREE);
            repository.save(sub);

            kafkaTemplate.send("subscription-invalidations-topic", sub.getUsername(), sub.getUsername());

            log.info("Подписка пользователя {} истекла. Отправлено событие инвалидации кэша в Kafka.", sub.getUsername());
        }
    }
}