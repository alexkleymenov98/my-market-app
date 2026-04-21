package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/import.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) 
public class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/items"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("text/html;charset=UTF-8"))
           .andExpect(view().name("items")) 
           .andExpect(model().attributeExists("items")); 
    }

    @Test
    void testGetProduct() throws Exception {
        mockMvc.perform(get("/items/1"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("text/html;charset=UTF-8"))
           .andExpect(view().name("item")) 
           .andExpect(model().attributeExists("item")); 
    }
}
