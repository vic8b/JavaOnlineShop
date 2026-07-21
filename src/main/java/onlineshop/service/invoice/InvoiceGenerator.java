package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;

public interface InvoiceGenerator {
    Invoice generate(Order order);
}
