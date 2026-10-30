package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductController;
import com.rbdip.bookstore.product.ProductRepository;
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
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController controller;

    private Product productWithId() {
        Product product = new Product("Clean Code", new BigDecimal("35.00"), "A book");
        ReflectionTestUtils.setField(product, "id", 1L);
        return product;
    }

    @Test
    void createProductReturnsIdAndName() {
        when(productRepository.save(any(Product.class))).thenReturn(productWithId());

        Map<String, Object> result = controller.createProduct(
                Map.of("name", "Clean Code", "price", "35.00", "description", "A book"));

        assertThat(result).containsEntry("id", 1L).containsEntry("name", "Clean Code");
    }

    @Test
    void listProductsReturnsNameAndPrice() {
        when(productRepository.findAll()).thenReturn(List.of(productWithId()));

        List<Map<String, Object>> result = controller.listProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("name", "Clean Code").containsEntry("price", "35.00");
    }
}