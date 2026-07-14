package onlineshop.domain.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Smartphone extends Product {
    @NonNull
    private final String color;
    @NonNull
    private final String batteryCapacity;

    private final List<String> accessories;

    @Builder
    public Smartphone(@NonNull String id, @NonNull String name, @NonNull BigDecimal price, int quantity, @NonNull String color, @NonNull String batteryCapacity, @Singular List<String> accessories) {
        super(id, name, price, quantity);
        this.color = color;
        this.batteryCapacity = batteryCapacity;
        this.accessories = accessories;
    }

    @Override
    public String toString() {
        return super.toString() + "\nColor: " + color + ", battery capacity: " + batteryCapacity + "\n"
                + "Accessories: " + accessories;
    }
}
