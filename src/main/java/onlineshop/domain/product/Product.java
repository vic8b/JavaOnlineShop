package onlineshop.domain.product;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public abstract class Product {
    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private BigDecimal price;
    private int quantity;

    protected Product(@NonNull String id, @NonNull String name, @NonNull BigDecimal price, int quantity) {
        validate(price, quantity);

        this.id = id;
        this.name = name;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > quantity) throw new IllegalArgumentException("Not enough products in stock");

        quantity -= amount;
    }

    public void changePrice(@NonNull BigDecimal newPrice) {
        if (newPrice.signum() <= 0) throw new IllegalArgumentException("Update price must be positive");

        price = newPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private static void validate(BigDecimal price, int quantity) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Price must be positive");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
    }

    @Override
    public String toString() {
        return "Product [" + id + "]: " + name + ", price: " + price + " [quantity: " + quantity + "]";
    }
}
