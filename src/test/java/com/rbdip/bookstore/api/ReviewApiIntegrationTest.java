package com.rbdip.bookstore.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.reference.AbstractIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReviewApiIntegrationTest extends AbstractIntegrationTest {

    private Long createProduct() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products", Map.of("name", "Clean Code", "price", new BigDecimal("35.00")), Map.class);
        return Long.valueOf(response.getBody().get("id").toString());
    }

    @Test
    void addsReview() {
        Long productId = createProduct();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products/" + productId + "/reviews",
                Map.of("authorName", "Anna", "rating", 5, "comment", "Great"),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
    }

    @Test
    void anonymousAuthorDefaultsToAnonymous() {
        Long productId = createProduct();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/products/" + productId + "/reviews",
                Map.of("rating", 4),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void listsReviews() {
        Long productId = createProduct();
        restTemplate.postForEntity(
                "/products/" + productId + "/reviews",
                Map.of("authorName", "Anna", "rating", 5, "comment", "Great"),
                Map.class);

        ResponseEntity<List> response = restTemplate.getForEntity("/products/" + productId + "/reviews", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        Map<?, ?> review = (Map<?, ?>) response.getBody().get(0);
        assertThat(review.get("authorName")).isEqualTo("Anna");
        assertThat(review.get("rating")).isEqualTo(5);
        assertThat(review.get("comment")).isEqualTo("Great");
    }

    @Test
    void listsReviewsForProductWithoutAny() {
        Long productId = createProduct();
        ResponseEntity<List> response = restTemplate.getForEntity("/products/" + productId + "/reviews", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}