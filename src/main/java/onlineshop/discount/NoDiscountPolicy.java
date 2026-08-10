package onlineshop.discount;

import lombok.NonNull;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class NoDiscountPolicy implements DiscountPolicy {
    @Override
    public BigDecimal calculateDiscount(@NonNull List<OrderItem> items, @NonNull BigDecimal regularPrice) {
        return BigDecimal.ZERO;
    }
}
