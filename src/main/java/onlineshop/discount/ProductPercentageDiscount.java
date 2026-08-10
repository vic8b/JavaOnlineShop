package onlineshop.discount;

import lombok.NonNull;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ProductPercentageDiscount implements DiscountPolicy {
    private final String productId;
    private final BigDecimal percentage;

    public ProductPercentageDiscount(@NonNull String productId, @NonNull BigDecimal percentage) {
        if (percentage.signum() <= 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }


        this.productId = productId;
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculateDiscount(@NonNull List<OrderItem> items, @NonNull BigDecimal regularPrice) {
        return items.stream()
                .filter(item -> item.product().getId().equals(productId))
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
