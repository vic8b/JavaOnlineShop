package onlineshop.domain.order;

import lombok.Builder;
import lombok.NonNull;
import onlineshop.domain.product.Product;

import java.math.BigDecimal;

public record OrderItem(@NonNull Product product, int quantity, @NonNull BigDecimal unitPrice) {
    // Design pattern: Builder
    @Builder
    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
    }

    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "Order item: " + product + ", quantity: " + quantity + ", price: " + unitPrice;
    }
}
