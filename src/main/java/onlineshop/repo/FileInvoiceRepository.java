package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.invoice.Invoice;
import onlineshop.exception.InvoiceAlreadyExistsException;
import onlineshop.exception.InvoicePersistenceException;
import onlineshop.repo.file.InvoiceFileSerializationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class FileInvoiceRepository implements InvoiceRepository {
    private final Map<UUID, Invoice> invoiceRepo = new ConcurrentHashMap<>();
    private final Path file;
    private final InvoiceFileSerializationService invoiceFileSerializationService;

    public FileInvoiceRepository(@NonNull Path file, @NonNull OrderRepository orderRepository) {
        this.file = file;
        this.invoiceFileSerializationService = new InvoiceFileSerializationService(orderRepository);

        initializeFile();
        loadInvoices();
    }

    @Override
    public synchronized void add(@NonNull Invoice invoice) {
        validateIfInvoiceAlreadyExists(invoice);

        Map<UUID, Invoice> updatedInvoices = new HashMap<>(invoiceRepo);
        updatedInvoices.put(invoice.getInvoiceId(), invoice);

        saveAll(updatedInvoices);

        invoiceRepo.put(invoice.getInvoiceId(), invoice);

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

        if (invoiceRepo.containsKey(invoice.getInvoiceId())) {
            throw InvoiceAlreadyExistsException.forId(invoice.getInvoiceId());
        }
    }

    private void initializeFile() {
        try {
            Path parent = file.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.exists(file) && Files.isDirectory(file)) {
                throw new InvoicePersistenceException("Expected a file, but path is directory");
            }

            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            throw new InvoicePersistenceException("Could not initialize invoices file", e);
        }
    }

    private void loadInvoices() {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.filter(line -> !line.isBlank())
                    .map(invoiceFileSerializationService::decode)
                    .forEach(this::addLoadedInvoice);
        } catch (IOException e) {
            throw new InvoicePersistenceException("Could not load invoices", e);
        }
    }

    private void saveAll(Map<UUID, Invoice> invoicesToSave) {
        List<String> lines = invoicesToSave.values().stream()
                .map(invoiceFileSerializationService::encode)
                .toList();

        try {
            Files.write(
                    file,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new InvoicePersistenceException("Could not save invoices to file: " + file, e);
        }
    }

    private void addLoadedInvoice(Invoice invoice) {
        if (invoiceRepo.containsKey(invoice.getInvoiceId())) {
            throw new InvoicePersistenceException("Duplicate invoice id in file: " + invoice.getInvoiceId());
        }

        if (findByNumber(invoice.getInvoiceNumber()).isPresent()) {
            throw new InvoicePersistenceException("Duplicate invoice number in file: " + invoice.getInvoiceNumber());
        }

        invoiceRepo.put(invoice.getInvoiceId(), invoice);
    }
}
