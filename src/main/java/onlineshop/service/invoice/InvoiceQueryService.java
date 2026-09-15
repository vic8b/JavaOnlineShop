package onlineshop.service.invoice;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.repo.InvoiceRepository;

import java.util.Comparator;
import java.util.List;

public class InvoiceQueryService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceQueryService(@NonNull InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> findInvoicesForAccount(@NonNull String accountId) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getOrder().getAccount().getAccountId().equals(accountId))
                .sorted(Comparator.comparingInt(this::invoiceSequence))
                .toList();
    }

    private int invoiceSequence(@NonNull Invoice invoice) {
        String invoiceNumber = invoice.getInvoiceNumber();

        return Integer.parseInt(invoiceNumber.substring(invoiceNumber.lastIndexOf('/') + 1));
    }
}
