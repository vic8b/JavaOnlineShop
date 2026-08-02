package onlineshop.service.invoice;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;

import java.time.Clock;
import java.time.Instant;

public class InvoiceService implements InvoiceGenerator {
    private final InvoiceNumberGenerator numberGenerator;
    private final Clock clock;

    public InvoiceService(@NonNull InvoiceNumberGenerator numberGenerator, @NonNull Clock clock) {
        this.numberGenerator = numberGenerator;
        this.clock = clock;
    }

    @Override
    public Invoice generate(@NonNull Order order) {
        String invoiceNumber = numberGenerator.generate();

        return Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .order(order)
                .issueDate(Instant.now(clock))
                .build();
    }
}
