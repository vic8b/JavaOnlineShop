package onlineshop.service.discount;

import lombok.NonNull;
import onlineshop.discount.DiscountPolicy;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PricingService {
    private final DiscountPolicy discountPolicy;

    public PricingService(@NonNull DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public BigDecimal calculateFinalPrice(@NonNull List<OrderItem> items) {
        if (items.isEmpty()) throw new IllegalArgumentException("Items cannot be empty");

        BigDecimal regularPrice = items.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = discountPolicy.calculateDiscount(items, regularPrice);

        if (discount.signum() < 0)
            throw new IllegalStateException("Discount cannot be negative");

        if (discount.compareTo(regularPrice) > 0)
            throw new IllegalStateException("Discount cannot exceed regular price");

        return regularPrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
}
