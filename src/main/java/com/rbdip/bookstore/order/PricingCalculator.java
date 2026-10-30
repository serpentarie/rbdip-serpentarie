package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PricingCalculator {

    public record LineItem(BigDecimal price, int quantity) {
    }

    private static final int BULK_QUANTITY_THRESHOLD = 10;
    private static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.95");
    private static final BigDecimal VIP_DISCOUNT_RATE = new BigDecimal("0.90");
    private static final BigDecimal WHOLESALE_DISCOUNT_RATE = new BigDecimal("0.85");
    private static final BigDecimal FIXED_COUPON_AMOUNT = new BigDecimal("10.00");
    private static final BigDecimal PERCENT_COUPON_RATE = new BigDecimal("0.80");
    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("1000.00");
    private static final BigDecimal LARGE_ORDER_RATE = new BigDecimal("0.98");

    private static final String VIP_CUSTOMER = "vip";
    private static final String WHOLESALE_CUSTOMER = "wholesale";
    private static final String FIXED_COUPON = "SAVE10";
    private static final String PERCENT_COUPON = "SAVE20PERCENT";

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = sumLinePrices(items);
        total = applyCustomerDiscount(total, customerType);
        total = applyCoupon(total, couponCode);
        total = clampToZero(total);
        total = applyLargeOrderRate(total);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumLinePrices(List<LineItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            if (item.quantity() > BULK_QUANTITY_THRESHOLD) {
                linePrice = linePrice.multiply(BULK_DISCOUNT_RATE);
            }
            total = total.add(linePrice);
        }
        return total;
    }

    private BigDecimal applyCustomerDiscount(BigDecimal total, String customerType) {
        if (VIP_CUSTOMER.equals(customerType)) {
            return total.multiply(VIP_DISCOUNT_RATE);
        }
        if (WHOLESALE_CUSTOMER.equals(customerType)) {
            return total.multiply(WHOLESALE_DISCOUNT_RATE);
        }
        return total;
    }

    private BigDecimal applyCoupon(BigDecimal total, String couponCode) {
        if (FIXED_COUPON.equals(couponCode)) {
            return total.subtract(FIXED_COUPON_AMOUNT);
        }
        if (PERCENT_COUPON.equals(couponCode)) {
            return total.multiply(PERCENT_COUPON_RATE);
        }
        return total;
    }

    private BigDecimal clampToZero(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    private BigDecimal applyLargeOrderRate(BigDecimal total) {
        if (total.compareTo(LARGE_ORDER_THRESHOLD) > 0) {
            return total.multiply(LARGE_ORDER_RATE);
        }
        return total;
    }
}