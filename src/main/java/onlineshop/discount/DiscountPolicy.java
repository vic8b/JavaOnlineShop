package onlineshop.discount;

import lombok.NonNull;
import onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountPolicy {
    BigDecimal calculateDiscount(@NonNull List<OrderItem> items, @NonNull BigDecimal regularPrice);
}
