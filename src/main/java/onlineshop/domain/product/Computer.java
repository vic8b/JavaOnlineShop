package onlineshop.domain.product;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Computer extends Product {
    @NonNull
    private final String cpu;
    @NonNull
    private final String ram;

    // Design pattern: Builder
    @Builder
    public Computer(@NonNull String id, @NonNull String name, @NonNull BigDecimal price, int quantity, @NonNull String cpu, @NonNull String ram) {
        super(id, name, price, quantity);
        this.cpu = cpu;
        this.ram = ram;
    }

    @Override
    public String toString() {
        return super.toString() + "\nCPU: " + cpu + ", RAM: " + ram;
    }
}

