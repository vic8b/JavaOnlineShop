package onlineshop.discount;

import lombok.NonNull;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderValuePercentageDiscount implements DiscountPolicy {
    private final BigDecimal minimumValue;
    private final BigDecimal percentage;

    public OrderValuePercentageDiscount(@NonNull BigDecimal minimumValue, @NonNull BigDecimal percentage) {
        validateDiscountParameters(minimumValue, percentage);

        this.minimumValue = minimumValue;
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculateDiscount(@NonNull List<OrderItem> items, @NonNull BigDecimal regularPrice) {
        if (regularPrice.compareTo(minimumValue) < 0) {
            return BigDecimal.ZERO;
        }

        return regularPrice.multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private static void validateDiscountParameters(BigDecimal minimumValue, BigDecimal percentage) {
        if (minimumValue.signum() < 0) {
            throw new IllegalArgumentException("Minimum value cannot be negative");
        }
        if (percentage.signum() <= 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }
}
