package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    void add(@NonNull Invoice invoice);
    Optional<Invoice> findById(@NonNull UUID id);
    Optional<Invoice> findByNumber(@NonNull String invoiceNumber);
    Optional<Invoice> findByOrderId(@NonNull UUID orderId);
    List<Invoice> findAll();
}
