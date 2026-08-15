package onlineshop.domain.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Electronics extends Product {
    // Design pattern: Builder
    @Builder
    public Electronics(@NonNull String id, @NonNull String name, @NonNull BigDecimal price, int quantity) {
        super(id, name, price, quantity);
    }
}
