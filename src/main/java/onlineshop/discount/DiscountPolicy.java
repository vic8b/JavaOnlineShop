package onlineshop.discount;

import lombok.NonNull;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// Discount calculation contract:
// Each policy calculates its discount independently using the original order data.
// Combined policies are not applied sequentially to an already discounted price.
// The total combined discount cannot exceed the regular order price.
//
// Design patterns:
// Strategy - defines interchangeable discount calculation algorithms.
// Decorator - andThen() composes multiple discount policies
// while preserving the same DiscountPolicy contract.

@FunctionalInterface
public interface DiscountPolicy {
    BigDecimal calculateDiscount(@NonNull List<OrderItem> items, @NonNull BigDecimal regularPrice);

    default DiscountPolicy andThen(@NonNull DiscountPolicy nextPolicy) {
        return (items, regularPrice) -> {
            BigDecimal currentDiscount = calculateDiscount(items, regularPrice);

            BigDecimal nextDiscount = nextPolicy.calculateDiscount(items, regularPrice);

            BigDecimal totalDiscount = currentDiscount.add(nextDiscount).setScale(2, RoundingMode.HALF_UP);

            return totalDiscount.min(regularPrice);
        };
    }
}
