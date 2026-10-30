package com.rbdip.bookstore.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.reference.AbstractIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class OrderApiIntegrationTest extends AbstractIntegrationTest {

    private Long createProduct(String name, String price) {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products", Map.of("name", name, "price", new BigDecimal(price)), Map.class);
        return Long.valueOf(response.getBody().get("id").toString());
    }

    private ResponseEntity<Map> placeOrder(Long productId, int quantity, String customerType, String coupon) {
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", "+79990000000", customerType, coupon,
                List.of(new CreateOrderRequest.Item(productId, quantity)));
        return restTemplate.postForEntity("/orders", request, Map.class);
    }

    @Test
    void createsOrderHappyPath() {
        Long productId = createProduct("Clean Code", "35.00");
        ResponseEntity<Map> response = placeOrder(productId, 2, "regular", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("new");
    }

    @Test
    void createsVipOrder() {
        Long productId = createProduct("Refactoring", "40.00");
        assertThat(placeOrder(productId, 1, "vip", null).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void createsWholesaleOrderWithCoupon() {
        Long productId = createProduct("DDD", "50.00");
        assertThat(placeOrder(productId, 12, "wholesale", "SAVE20PERCENT").getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void createsOrderWithNullQuantityDefaultsToOne() {
        Long productId = createProduct("Pragmatic", "45.00");
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "regular", null,
                List.of(new CreateOrderRequest.Item(productId, null)));
        ResponseEntity<Map> response = restTemplate.postForEntity("/orders", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void rejectsOrderWithoutItems() {
        Long productId = createProduct("Clean Code", "35.00");
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "regular", null, List.of());
        ResponseEntity<Map> response = restTemplate.postForEntity("/orders", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsOrderWithUnknownProduct() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "regular", null,
                List.of(new CreateOrderRequest.Item(99999L, 1)));
        ResponseEntity<Map> response = restTemplate.postForEntity("/orders", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void rejectsOrderWithNonPositiveQuantity() {
        Long productId = createProduct("Clean Code", "35.00");
        ResponseEntity<Map> response = placeOrder(productId, 0, "regular", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listsOrdersWithItems() {
        Long productId = createProduct("Clean Code", "35.00");
        placeOrder(productId, 2, "regular", null);

        ResponseEntity<List> response = restTemplate.getForEntity("/orders", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        Map<?, ?> order = (Map<?, ?>) response.getBody().get(0);
        assertThat(order.get("customerFullName")).isEqualTo("Ivan Petrov");
        assertThat((List<?>) order.get("items")).hasSize(1);
    }
}