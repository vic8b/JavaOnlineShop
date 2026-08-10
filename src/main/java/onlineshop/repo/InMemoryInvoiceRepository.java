package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.invoice.Invoice;
import onlineshop.exception.InvoiceAlreadyExistsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryInvoiceRepository implements InvoiceRepository {
    private final Map<UUID, Invoice> invoiceRepo = new ConcurrentHashMap<>();

    @Override
    public synchronized void add(@NonNull Invoice invoice) {
        validateIfInvoiceAlreadyExists(invoice);

        log.info("Invoice: {} has been added to the repository", invoice.getInvoiceNumber());
    }

    @Override
    public Optional<Invoice> findById(@NonNull UUID id) {
        return Optional.ofNullable(invoiceRepo.get(id));
    }

    @Override
    public Optional<Invoice> findByNumber(@NonNull String invoiceNumber) {
        return invoiceRepo.values().stream()
                .filter(invoice -> invoice.getInvoiceNumber().equals(invoiceNumber))
                .findFirst();
    }

    @Override
    public Optional<Invoice> findByOrderId(@NonNull UUID orderId) {
        return invoiceRepo.values().stream()
                .filter(invoice -> invoice.getOrder().getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Invoice> findAll() {
        return List.copyOf(invoiceRepo.values());
    }

    private void validateIfInvoiceAlreadyExists(Invoice invoice) {
        if (findByNumber(invoice.getInvoiceNumber()).isPresent()) {
            throw InvoiceAlreadyExistsException.forNumber(invoice.getInvoiceNumber());
        }

        if (invoiceRepo.putIfAbsent(invoice.getInvoiceId(), invoice) != null) {
            throw InvoiceAlreadyExistsException.forId(invoice.getInvoiceId());
        }
    }
}
