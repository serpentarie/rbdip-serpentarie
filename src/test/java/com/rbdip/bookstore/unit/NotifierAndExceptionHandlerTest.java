package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.GlobalExceptionHandler;
import com.rbdip.bookstore.order.OrderNotifier;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NotifierAndExceptionHandlerTest {

    @Test
    void sendConfirmationRunsWithoutError() {
        OrderNotifier notifier = new OrderNotifier();
        notifier.sendConfirmation("Ivan Petrov", 1L, new BigDecimal("10.00"));
    }

    @Test
    void validationErrorMapsToBadRequest() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<Map<String, String>> response =
                handler.handleValidation(new IllegalArgumentException("quantity must be positive"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "quantity must be positive");
    }
}