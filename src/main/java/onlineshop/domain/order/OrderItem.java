package onlineshop.domain.order;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.product.Product;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
public class OrderItem {
    @NonNull
    private final Product product;
    private final int quantity;
    @NonNull
    private final BigDecimal unitPrice;

    @Builder
    public OrderItem(@NonNull Product product, int quantity, @NonNull BigDecimal unitPrice) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice.signum() <= 0) throw new IllegalArgumentException("Unit price must be positive");

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "Order item: " + product + ", quantity: " + quantity + ", price: " + unitPrice;
    }
}
