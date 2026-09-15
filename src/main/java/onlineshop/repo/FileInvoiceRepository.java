package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.exception.InvoiceAlreadyExistsException;
import onlineshop.exception.InvoicePersistenceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class FileInvoiceRepository implements InvoiceRepository {
    private final Map<UUID, Invoice> invoiceRepo = new HashMap<>();
    private final Path file;
    private final OrderRepository orderRepository;

    public FileInvoiceRepository(@NonNull Path file, @NonNull OrderRepository orderRepository) {
        this.file = file;
        this.orderRepository = orderRepository;

        initializeFile();
        loadInvoices();
    }

    @Override
    public void add(@NonNull Invoice invoice) {
        if (findByNumber(invoice.getInvoiceNumber()).isPresent()) {
            throw InvoiceAlreadyExistsException.forNumber(invoice.getInvoiceNumber());
        }

        if (invoiceRepo.containsKey(invoice.getInvoiceId())) {
            throw InvoiceAlreadyExistsException.forId(invoice.getInvoiceId());
        }

        Map<UUID, Invoice> updatedInvoices = new HashMap<>(invoiceRepo);
        updatedInvoices.put(invoice.getInvoiceId(), invoice);

        saveAll(updatedInvoices);

        invoiceRepo.put(invoice.getInvoiceId(), invoice);

        System.out.println("Invoice: " + invoice.getInvoiceNumber() + " has been added to the repository");
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
                    .map(this::deserializeInvoice)
                    .forEach(this::addLoadedInvoice);
        } catch (IOException e) {
            throw new InvoicePersistenceException("Could not load invoices", e);
        }
    }

    private void saveAll(Map<UUID, Invoice> invoicesToSave) {
        List<String> lines = invoicesToSave.values().stream()
                .map(this::serializeInvoice)
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
            LocalDateTime invoiceIssueDate = LocalDateTime.parse(parts[3]);

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
            throw new InvoicePersistenceException("Could not deserialize invoice:" + invoiceLine, e);
        }
    }
}
