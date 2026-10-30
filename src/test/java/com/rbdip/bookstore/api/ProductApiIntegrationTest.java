package com.rbdip.bookstore.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.reference.AbstractIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ProductApiIntegrationTest extends AbstractIntegrationTest {

    @Test
    void createsProduct() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products", Map.of("name", "Clean Code", "price", new BigDecimal("35.00")), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("Clean Code");
    }

    @Test
    void listsProducts() {
        restTemplate.postForEntity(
                "/products", Map.of("name", "Refactoring", "price", new BigDecimal("40.00")), Map.class);

        ResponseEntity<List> response = restTemplate.getForEntity("/products", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        Map<?, ?> product = (Map<?, ?>) response.getBody().get(0);
        assertThat(product.get("name")).isEqualTo("Refactoring");
        assertThat(product.get("price")).isEqualTo("40.00");
    }

    @Test
    void createsProductWithDescription() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products",
                Map.of("name", "DDD", "price", new BigDecimal("50.00"), "description", "Blue book"),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("name")).isEqualTo("DDD");
    }
}