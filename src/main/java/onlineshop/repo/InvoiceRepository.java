package onlineshop.repo;

import onlineshop.domain.invoice.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    void add(Invoice invoice);
    Optional<Invoice> findById(String id);
    Optional<Invoice> findByOrderId(String orderId);
    List<Invoice> findAll();
}
