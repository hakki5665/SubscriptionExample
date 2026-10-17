package com.example.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Spring Application Context Smoke Test")
class SubscriptionServiceApplicationTests extends BaseSubscriptionIntegrationTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	@DisplayName("Should successfully load application context and initialize all enterprise beans")
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}
}