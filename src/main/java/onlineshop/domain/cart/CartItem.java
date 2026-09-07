package onlineshop.domain.cart;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.product.Product;

@Getter
@EqualsAndHashCode
public class CartItem {
    @NonNull
    private final Product product;
    @NonNull
    private int quantity;

    // Design pattern: Builder
    @Builder
    public CartItem(@NonNull Product product, @NonNull int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Required quantity exceeds item availability");
        }

        this.product = product;
        this.quantity = quantity;
    }

    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (newQuantity > product.getQuantity()) {
            throw new IllegalArgumentException("Required quantity exceeds item availability");
        }

        this.quantity = newQuantity;
    }

    @Override
    public String toString() {
        return "Cart item: " + product + ", quantity: " + quantity;
    }
}
