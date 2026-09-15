package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.exception.InvoiceAlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryInvoiceRepository implements InvoiceRepository {
    private final Map<String, Invoice> invoiceRepo = new HashMap<>();

    @Override
    public void add(@NonNull Invoice invoice) {
        if (invoiceRepo.putIfAbsent(invoice.getInvoiceId(), invoice) != null) {
            throw new InvoiceAlreadyExistsException(invoice.getInvoiceId());
        }

        System.out.println("Invoice: " + invoice.getInvoiceId() + " has been added to the repository");
    }

    @Override
    public Optional<Invoice> findById(@NonNull String id) {
        return Optional.ofNullable(invoiceRepo.get(id));
    }

    @Override
    public Optional<Invoice> findByOrderId(@NonNull String orderId) {
        return invoiceRepo.values().stream()
                .filter(invoice -> invoice.getOrder().getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Invoice> findAll() {
        return List.copyOf(invoiceRepo.values());
    }
}
