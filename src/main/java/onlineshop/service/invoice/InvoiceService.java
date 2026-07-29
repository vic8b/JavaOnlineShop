package onlineshop.service.invoice;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;

public class InvoiceService implements InvoiceGenerator {
    private final InvoiceNumberGenerator numberGenerator;

    public InvoiceService(@NonNull InvoiceNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Override
    public Invoice generate(@NonNull Order order) {
        String invoiceNumber = numberGenerator.generate();

        return Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .order(order)
                .build();
    }
}
