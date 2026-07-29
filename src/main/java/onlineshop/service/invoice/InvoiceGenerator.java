package onlineshop.service.invoice;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;

public interface InvoiceGenerator {
    Invoice generate(@NonNull Order order);
}
