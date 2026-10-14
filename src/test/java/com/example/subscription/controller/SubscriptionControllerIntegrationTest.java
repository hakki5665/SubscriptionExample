package com.example.subscription.controller;

import com.example.subscription.BaseSubscriptionIntegrationTest;
import com.example.subscription.entity.UserSubscription;
import com.example.subscription.entity.SubscriptionType;
import com.example.subscription.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Subscription REST API Contract Tests")
class SubscriptionControllerIntegrationTest extends BaseSubscriptionIntegrationTest {

    @Autowired
    private UserSubscriptionRepository subscriptionRepository;

    @BeforeEach
    void cleanDatabase() {
        subscriptionRepository.deleteAll();
    }

    @Test
    @DisplayName("Get active subscription -> return 200 OK and valid DTO")
    void shouldReturnActiveSubscriptionSuccessfully() throws Exception {
        String username = "active_pro_user";

        UserSubscription entity = new UserSubscription();
        entity.setUsername(username);
        entity.setSubscriptionType(SubscriptionType.PREMIUM);
        entity.setExpiresAt(LocalDateTime.now().plusDays(30));
        subscriptionRepository.save(entity);

        MvcResult result = mockMvc.perform(get("/api/v1/subscriptions/" + username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        SubscriptionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                SubscriptionResponse.class
        );

        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getSubscriptionType()).isEqualTo("PREMIUM");
    }

    @Test
    @DisplayName("Get non-existent subscription -> return FREE fallback type")
    void shouldReturnFreeTypeWhenSubscriptionNotFound() throws Exception {
        String username = "new_unknown_user";

        MvcResult result = mockMvc.perform(get("/api/v1/subscriptions/" + username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        SubscriptionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                SubscriptionResponse.class
        );

        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getSubscriptionType()).isEqualTo("FREE");
    }
}