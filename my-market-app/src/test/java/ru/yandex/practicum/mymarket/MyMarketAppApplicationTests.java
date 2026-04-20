package ru.yandex.practicum.mymarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;


class MyMarketAppApplicationTests extends AbstractTestContainersTest{

	@Test
	void contextLoads() {
		System.out.println("=== Контекст успешно загружен! ===");
        System.out.println("URL БД: " +  this.POSTGRES_CONTAINER.getJdbcUrl());
	}

}
