package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.review.Review;
import com.rbdip.bookstore.review.ReviewController;
import com.rbdip.bookstore.review.ReviewService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController controller;

    @Test
    void addReviewReturnsCreatedId() {
        Review review = new Review(1L, "Anna", 5, "Great");
        ReflectionTestUtils.setField(review, "id", 10L);
        when(reviewService.addReview(1L, "Anna", 5, "Great")).thenReturn(review);

        Map<String, Object> result = controller.addReview(
                1L, Map.of("authorName", "Anna", "rating", 5, "comment", "Great"));

        assertThat(result).containsEntry("id", 10L);
    }

    @Test
    void listReviewsReturnsAuthorAndRating() {
        when(reviewService.listReviews(1L)).thenReturn(List.of(new Review(1L, "Anna", 5, "Great")));

        List<Map<String, Object>> result = controller.listReviews(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsEntry("authorName", "Anna").containsEntry("rating", 5);
    }
}