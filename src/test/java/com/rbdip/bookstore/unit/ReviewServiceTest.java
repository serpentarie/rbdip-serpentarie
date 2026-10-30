package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.order.OrderItemRepository;
import com.rbdip.bookstore.order.OrderRepository;
import com.rbdip.bookstore.review.Review;
import com.rbdip.bookstore.review.ReviewRepository;
import com.rbdip.bookstore.review.ReviewService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewService service;

    @Test
    void addReviewPersistsAuthorAndComment() {
        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderItemRepository.findAll()).thenReturn(List.of());
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = service.addReview(1L, "Anna", 5, "Great");

        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getAuthorName()).isEqualTo("Anna");
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReviewDefaultsAnonymousAuthor() {
        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderItemRepository.findAll()).thenReturn(List.of());
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = service.addReview(1L, null, 4, null);

        assertThat(result.getAuthorName()).isEqualTo("anonymous");
    }

    @Test
    void listReviewsDelegatesToRepository() {
        when(reviewRepository.findByProductId(1L)).thenReturn(List.of(new Review(1L, "Anna", 5, "Great")));

        List<Review> result = service.listReviews(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthorName()).isEqualTo("Anna");
    }
}