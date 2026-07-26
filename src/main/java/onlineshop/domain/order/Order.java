package onlineshop.domain.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.useraccount.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@EqualsAndHashCode
public class Order {
    @NonNull
    private final String orderId;
    @NonNull
    private final Account account;
    @NonNull
    private final List<OrderItem> items;
    @NonNull
    private final BigDecimal totalPrice;
    @NonNull
    private final LocalDateTime orderDate;
    @NonNull
    private OrderStatus orderStatus;

    @Builder
    public Order(@NonNull String orderId, @NonNull Account account, @NonNull List<OrderItem> items) {
        if (orderId.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
        if (items.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");

        this.orderId = orderId;
        this.account = account;
        this.items = List.copyOf(items);
        this.totalPrice = calculateTotalPrice();
        this.orderDate = LocalDateTime.now();
        this.orderStatus = OrderStatus.PENDING;
    }

    private BigDecimal calculateTotalPrice() {
        return this.items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "[" + orderId + "]";
    }
}
