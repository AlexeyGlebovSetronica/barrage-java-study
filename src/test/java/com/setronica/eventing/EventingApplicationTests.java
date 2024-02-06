package com.setronica.eventing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest
class EventingApplicationTests {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:15-alpine"
	);

	@BeforeAll
	static void beforeAll() {
		// starting DB container before tests
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		// stopping DB container
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		// setting DB connection params
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Test
	void contextLoads() {
	}

}
