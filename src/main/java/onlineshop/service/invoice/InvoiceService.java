package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;

import java.time.LocalDateTime;

public class InvoiceService implements InvoiceGenerator {
    // TODO invoiceId logic to change
    private static int counter = 1;

    @Override
    public Invoice generate(Order order) {
        String invoiceNumber = "INV-" + LocalDateTime.now().getYear() + "/" + counter++;

        return Invoice.builder()
                .invoiceId(invoiceNumber)
                .order(order)
                .build();
    }
}
