package ru.yandex.practicum.mymarket;

import org.junit.jupiter.api.Test;

class MyMarketAppApplicationTests extends AbstractTestContainersTest{

	@Test
	void contextLoads() {
		System.out.println("=== Контекст успешно загружен! ===");
        System.out.println("URL БД: " +  this.POSTGRES_CONTAINER.getJdbcUrl());
	}

}
