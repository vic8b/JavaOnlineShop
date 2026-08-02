package onlineshop.domain.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.useraccount.Account;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @NonNull
    @EqualsAndHashCode.Include
    private final UUID orderId;
    @NonNull
    private final Account account;
    @NonNull
    private final List<OrderItem> items;
    @NonNull
    private final BigDecimal totalPrice;
    @NonNull
    private final Instant orderDate;
    @NonNull
    private OrderStatus orderStatus;

    public Order(
            @NonNull UUID orderId,
            @NonNull Account account,
            @NonNull List<OrderItem> items,
            @NonNull Instant orderDate,
            @NonNull OrderStatus orderStatus
    ) {
        if (items.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");

        this.orderId = orderId;
        this.account = account;
        this.items = List.copyOf(items);
        this.totalPrice = calculateTotalPrice();
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    @Builder
    public Order(@NonNull Account account, @NonNull List<OrderItem> items, @NonNull Instant orderDate) {
        if (items.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");

        this.orderId = UUID.randomUUID();
        this.account = account;
        this.items = List.copyOf(items);
        this.totalPrice = calculateTotalPrice();
        this.orderDate = orderDate;
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
