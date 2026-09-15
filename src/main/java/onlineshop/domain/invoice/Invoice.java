package onlineshop.domain.invoice;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.order.Order;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class Invoice {
    @NonNull
    private final String invoiceId;
    @NonNull
    private final Order order;
    @NonNull
    private final LocalDateTime issueDate;

    @Builder
    public Invoice(@NonNull String invoiceId, @NonNull Order order) {
        if (invoiceId.isBlank()) throw new IllegalArgumentException("ID cannot be blank");

        this.invoiceId = invoiceId;
        this.order = order;
        this.issueDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[" + invoiceId + "]";
    }
}
