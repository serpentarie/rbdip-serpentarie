package com.rbdip.bookstore.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.PricingCalculator;
import com.rbdip.bookstore.order.PricingCalculator.LineItem;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {

    private final PricingCalculator calculator = new PricingCalculator();

    private BigDecimal total(String price, int quantity) {
        return calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal(price), quantity)), "regular", null);
    }

    @Test
    void simpleRegularOrderIsSumOfLinePrices() {
        BigDecimal sum = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("10.00"), 1), new LineItem(new BigDecimal("20.00"), 2)),
                "regular", null);
        assertThat(sum).isEqualByComparingTo("50.00");
    }

    @Test
    void bulkDiscountAppliedAboveTenItems() {
        assertThat(total("10.00", 11)).isEqualByComparingTo("104.50");
    }

    @Test
    void noBulkDiscountAtExactlyTenItems() {
        assertThat(total("10.00", 10)).isEqualByComparingTo("100.00");
    }

    @Test
    void vipCustomerGetsTenPercentOff() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "vip", null);
        assertThat(value).isEqualByComparingTo("90.00");
    }

    @Test
    void wholesaleCustomerGetsFifteenPercentOff() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "wholesale", null);
        assertThat(value).isEqualByComparingTo("85.00");
    }

    @Test
    void unknownCustomerTypeGetsNoDiscount() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "gold", null);
        assertThat(value).isEqualByComparingTo("100.00");
    }

    @Test
    void save10CouponSubtractsFixedAmount() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE10");
        assertThat(value).isEqualByComparingTo("90.00");
    }

    @Test
    void save20PercentCouponAppliesPercentage() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE20PERCENT");
        assertThat(value).isEqualByComparingTo("80.00");
    }

    @Test
    void unknownCouponAppliesNoDiscount() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 1)), "regular", "NOPE");
        assertThat(value).isEqualByComparingTo("100.00");
    }

    @Test
    void negativeTotalIsClampedToZero() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("5.00"), 1)), "regular", "SAVE10");
        assertThat(value).isEqualByComparingTo("0.00");
    }

    @Test
    void largeOrderGetsAdditionalTwoPercentOff() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("600.00"), 2)), "regular", null);
        assertThat(value).isEqualByComparingTo("1176.00");
    }

    @Test
    void largeOrderExactlyAtThresholdGetsNoExtraDiscount() {
        assertThat(total("500.00", 2)).isEqualByComparingTo("1000.00");
    }

    @Test
    void combinedDiscountsStackInOrder() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("100.00"), 11)), "vip", "SAVE20PERCENT");
        assertThat(value).isEqualByComparingTo("752.40");
    }

    @Test
    void resultIsRoundedToTwoDecimals() {
        BigDecimal value = calculator.calculateOrderTotal(
                List.of(new LineItem(new BigDecimal("33.33"), 1)), "wholesale", null);
        assertThat(value).isEqualByComparingTo("28.33");
    }
}