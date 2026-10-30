package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.Order;
import com.rbdip.bookstore.order.OrderItem;
import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.review.Review;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EntityGettersTest {

    @Test
    void orderExposesConstructorState() {
        Order order = new Order("Ivan Petrov", "Moscow", "+7999", "new");
        assertThat(order.getId()).isNull();
        assertThat(order.getCustomerFullName()).isEqualTo("Ivan Petrov");
        assertThat(order.getCustomerAddress()).isEqualTo("Moscow");
        assertThat(order.getCustomerPhone()).isEqualTo("+7999");
        assertThat(order.getStatus()).isEqualTo("new");
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void orderItemExposesConstructorState() {
        OrderItem item = new OrderItem(5L, "Clean Code", new BigDecimal("35.00"), 3);
        assertThat(item.getId()).isNull();
        assertThat(item.getOrderId()).isEqualTo(5L);
        assertThat(item.getProductName()).isEqualTo("Clean Code");
        assertThat(item.getProductPrice()).isEqualByComparingTo("35.00");
        assertThat(item.getQuantity()).isEqualTo(3);
    }

    @Test
    void productExposesConstructorState() {
        Product product = new Product("Refactoring", new BigDecimal("40.00"), "A classic");
        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("Refactoring");
        assertThat(product.getPrice()).isEqualByComparingTo("40.00");
        assertThat(product.getDescription()).isEqualTo("A classic");
    }

    @Test
    void reviewExposesConstructorState() {
        Review review = new Review(2L, "Anna", 5, "Great");
        assertThat(review.getId()).isNull();
        assertThat(review.getProductId()).isEqualTo(2L);
        assertThat(review.getAuthorName()).isEqualTo("Anna");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Great");
    }
}