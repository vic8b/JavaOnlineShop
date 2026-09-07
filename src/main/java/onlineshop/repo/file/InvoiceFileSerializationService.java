package onlineshop.repo.file;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.exception.InvoicePersistenceException;
import onlineshop.repo.OrderRepository;

import java.time.Instant;
import java.util.UUID;

public class InvoiceFileSerializationService {
    private final OrderRepository orderRepository;

    public InvoiceFileSerializationService(@NonNull OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String encode(Invoice invoice) {
        return serializeInvoice(invoice);
    }

    public Invoice decode(String line) {
        return deserializeInvoice(line);
    }

    private String serializeInvoice(Invoice invoice) {
        return String.join(
                "|",
                invoice.getInvoiceId().toString(),
                invoice.getInvoiceNumber(),
                invoice.getOrder().getOrderId().toString(),
                invoice.getIssueDate().toString()
        );
    }

    private Invoice deserializeInvoice(String invoiceLine) {
        try {
            String[] parts = invoiceLine.split("\\|", -1);

            if (parts.length != 4) {
                throw new InvoicePersistenceException("Invalid invoice record: " + invoiceLine);
            }

            UUID invoiceId = UUID.fromString(parts[0]);
            String invoiceNumber = parts[1];
            UUID orderId = UUID.fromString(parts[2]);
            Instant invoiceIssueDate = Instant.parse(parts[3]);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new InvoicePersistenceException(
                            "Order " + orderId + " referenced by invoice " + invoiceId + " does not exist"
                    ));

            return new Invoice(
                    invoiceId,
                    invoiceNumber,
                    order,
                    invoiceIssueDate
            );
        } catch (InvoicePersistenceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InvoicePersistenceException("Could not deserialize invoice: " + invoiceLine, e);
        }
    }
}
