package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.order.CreateOrderRequest;
import com.rbdip.bookstore.order.Order;
import com.rbdip.bookstore.order.OrderController;
import com.rbdip.bookstore.order.OrderItem;
import com.rbdip.bookstore.order.OrderItemRepository;
import com.rbdip.bookstore.order.OrderRepository;
import com.rbdip.bookstore.order.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderController controller;

    private Order orderWithId() {
        Order order = new Order("Ivan Petrov", "Moscow", "+7999", "new");
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    @Test
    void createOrderReturnsIdAndStatus() {
        CreateOrderRequest request = new CreateOrderRequest(
                "Ivan Petrov", "Moscow", null, "regular", null,
                List.of(new CreateOrderRequest.Item(1L, 2)));
        when(orderService.createOrder(request)).thenReturn(orderWithId());

        Map<String, Object> result = controller.createOrder(request);

        assertThat(result).containsEntry("id", 1L).containsEntry("status", "new");
    }

    @Test
    void listOrdersIncludesNestedItems() {
        Order order = orderWithId();
        OrderItem item = new OrderItem(1L, "Clean Code", new BigDecimal("35.00"), 2);
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(item));

        List<Map<String, Object>> result = controller.listOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("customerFullName", "Ivan Petrov");
        List<?> items = (List<?>) result.get(0).get("items");
        assertThat(items).hasSize(1);
        assertThat((Map<?, ?>) items.get(0)).containsEntry("productName", "Clean Code").containsEntry("quantity", 2);
    }
}