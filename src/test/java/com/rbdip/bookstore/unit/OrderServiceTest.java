package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.order.Order;
import com.rbdip.bookstore.order.OrderItemRepository;
import com.rbdip.bookstore.order.OrderNotifier;
import com.rbdip.bookstore.order.OrderRepository;
import com.rbdip.bookstore.order.OrderService;
import com.rbdip.bookstore.order.OrderValidator;
import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private OrderNotifier orderNotifier;

    @InjectMocks
    private OrderService service;

    private CreateOrderRequest request(int quantity) {
        return new CreateOrderRequest(
                "Ivan Petrov", "Moscow", "+7999", "regular", null,
                List.of(new CreateOrderRequest.Item(1L, quantity)));
    }

    @Test
    void createOrderPersistsAndNotifies() {
        Product product = new Product("Clean Code", new BigDecimal("35.00"), null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Order saved = new Order("Ivan Petrov", "Moscow", "+7999", "new");
        ReflectionTestUtils.setField(saved, "id", 1L);
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = service.createOrder(request(2));

        assertThat(result).isSameAs(saved);
        verify(orderItemRepository).save(any());
        verify(orderNotifier).sendConfirmation(eq("Ivan Petrov"), eq(1L), any(BigDecimal.class));
    }

    @Test
    void createOrderThrowsWhenProductMissing() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(request(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}