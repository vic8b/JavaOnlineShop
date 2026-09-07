package onlineshop.domain.invoice;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.order.Order;

import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice {
    @NonNull
    @EqualsAndHashCode.Include
    private final UUID invoiceId;
    @NonNull
    private final String invoiceNumber;
    @NonNull
    private final Order order;
    @NonNull
    private final Instant issueDate;

    public Invoice(
            @NonNull UUID invoiceId,
            @NonNull String invoiceNumber,
            @NonNull Order order,
            @NonNull Instant issueDate
    ) {
        if (invoiceNumber.isBlank()) {
            throw new IllegalArgumentException("Invoice number cannot be blank");
        }

        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.order = order;
        this.issueDate = issueDate;
    }

    // Design pattern: Builder
    @Builder
    public Invoice(@NonNull String invoiceNumber, @NonNull Order order, @NonNull Instant issueDate) {
        if (invoiceNumber.isBlank()) {
            throw new IllegalArgumentException("Invoice number cannot be blank");
        }

        this.invoiceId = UUID.randomUUID();
        this.invoiceNumber = invoiceNumber;
        this.order = order;
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "[" + invoiceNumber + "]";
    }
}
