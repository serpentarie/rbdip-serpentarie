package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.order.OrderValidator;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderValidatorTest {

    private final OrderValidator validator = new OrderValidator();

    private CreateOrderRequest request(String name, String address, List<CreateOrderRequest.Item> items) {
        return new CreateOrderRequest(name, address, null, "regular", null, items);
    }

    @Test
    void acceptsValidRequest() {
        validator.validate(request("Ivan", "Moscow", List.of(new CreateOrderRequest.Item(1L, 2))));
    }

    @Test
    void rejectsBlankCustomerName() {
        assertThatThrownBy(() -> validator.validate(request("  ", "Moscow", List.of(new CreateOrderRequest.Item(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("customerFullName is required");
    }

    @Test
    void rejectsNullCustomerName() {
        assertThatThrownBy(() -> validator.validate(request(null, "Moscow", List.of(new CreateOrderRequest.Item(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankAddress() {
        assertThatThrownBy(() -> validator.validate(request("Ivan", null, List.of(new CreateOrderRequest.Item(1L, 1)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("customerAddress is required");
    }

    @Test
    void rejectsEmptyItems() {
        assertThatThrownBy(() -> validator.validate(request("Ivan", "Moscow", List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("order must contain at least one item");
    }

    @Test
    void rejectsNullItems() {
        assertThatThrownBy(() -> validator.validate(request("Ivan", "Moscow", null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> validator.validate(request("Ivan", "Moscow", List.of(new CreateOrderRequest.Item(1L, 0)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity must be positive");
    }

    @Test
    void acceptsNullQuantityAsOne() {
        validator.validate(request("Ivan", "Moscow", List.of(new CreateOrderRequest.Item(1L, null))));
    }
}