package onlineshop.repo;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.InvoiceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileInvoiceRepositoryTest {
    @TempDir
    Path tempDir;
    Path file;
    Account account;
    Product product;
    Order order;
    OrderItem orderItem;
    Order secondOrder;
    OrderItem secondOrderItem;
    Invoice invoice;
    InvoiceRepository invoiceRepository;
    OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        file = tempDir.resolve("invoices.txt");

        account = createTestAccount();

        product = createTestProduct();

        orderItem = createTestOrderItem();

        order = createTestOrder();

        secondOrderItem = createSecondTestOrderItem();

        secondOrder = createSecondTestOrder();

        orderRepository = new InMemoryOrderRepository();

        orderRepository.add(order);
        orderRepository.add(secondOrder);

        invoice = Invoice.builder()
                .invoiceNumber("INV-2026/1")
                .order(order)
                .build();
    }

    @Test
    void shouldCreateFilesWhenItDoesNotExist() {
        Path file = tempDir.resolve("invoices.txt");

        assertThat(file).doesNotExist();

        new FileInvoiceRepository(file, orderRepository);

        assertThat(file)
                .exists()
                .isRegularFile();
    }

    @Test
    void shouldAddInvoiceToTheRepository() {
        createRepository();

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findAll())
                .singleElement()
                .isEqualTo(invoice);

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .isPresent()
                .contains(invoice);
    }

    @Test
    void shouldLoadPreviouslySavedInvoice() {
        createRepository();

        invoiceRepository.add(invoice);

        FileInvoiceRepository reader = new FileInvoiceRepository(file, orderRepository);

        assertThat(reader.findById(invoice.getInvoiceId())).contains(invoice);
    }

    @Test
    void shouldLoadAllPreviouslySavedInvoices() {
        createRepository();

        Invoice secondInvoice = Invoice.builder()
                .invoiceNumber("INV-2026/2")
                .order(secondOrder)
                .build();

        invoiceRepository.add(invoice);
        invoiceRepository.add(secondInvoice);

        FileInvoiceRepository reader = new FileInvoiceRepository(file, orderRepository);

        assertThat(reader.findAll())
                .contains(invoice)
                .contains(secondInvoice);
    }

    @Test
    void shouldRejectDuplicateInvoiceNumber() {
        createRepository();

        Invoice invoiceDuplicate = Invoice.builder()
                .invoiceNumber("INV-2026/1")
                .order(order)
                .build();

        invoiceRepository.add(invoice);

        assertThatThrownBy(() -> invoiceRepository.add(invoiceDuplicate))
                .isInstanceOf(InvoiceAlreadyExistsException.class)
                .hasMessage("Invoice with number " + invoice.getInvoiceNumber() + " already exists");

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .contains(invoice);

        assertThat(invoiceRepository.findAll())
                .containsExactly(invoice);
    }

    @Test
    void shouldRejectDuplicateInvoiceId() {
        createRepository();

        UUID mockId = UUID.randomUUID();

        Invoice originalInvoice = new Invoice(mockId, "INV-2026/1", order, LocalDateTime.now());
        Invoice duplicateTestInvoice = new Invoice(mockId, "INV-2026/2", order, LocalDateTime.now());

        invoiceRepository.add(originalInvoice);

        assertThatThrownBy(() -> invoiceRepository.add(duplicateTestInvoice))
                .isInstanceOf(InvoiceAlreadyExistsException.class)
                .hasMessage("Invoice with id " + mockId + " already exists");

        assertThat(invoiceRepository.findById(mockId))
                .contains(originalInvoice);
    }

    @Test
    void shouldFindInvoiceById() {
        createRepository();

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .contains(invoice);
    }

    @Test
    void shouldFindInvoiceByNumber() {
        createRepository();

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findByNumber(invoice.getInvoiceNumber()))
                .contains(invoice);
    }

    @Test
    void shouldFindCorrectInvoiceByOrderId() {
        createRepository();

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findByOrderId(order.getOrderId()))
                .contains(invoice);
    }

    @Test
    void shouldFindAllInvoices() {
        createRepository();

        Invoice secondInvoice = Invoice.builder()
                .invoiceNumber("INV-2026/2")
                .order(secondOrder)
                .build();

        invoiceRepository.add(invoice);
        invoiceRepository.add(secondInvoice);

        assertThat(invoiceRepository.findAll())
                .contains(invoice)
                .contains(secondInvoice);
    }

    @Test
    void shouldReturnEmptyWhenInvoiceDoesNotExist() {
        createRepository();

        assertThat(invoiceRepository.findById(UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenInvoiceForOrderDoesNotExist() {
        createRepository();

        assertThat(invoiceRepository.findByOrderId(UUID.randomUUID()))
                .isEmpty();
    }

    private Account createTestAccount() {
        return Account.builder()
                .accountId("ACC-1")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
    }

    private Product createTestProduct() {
        return Electronics.builder()
                .id("E-1")
                .name("Test electronics")
                .price(BigDecimal.TEN)
                .quantity(10)
                .build();
    }

    private OrderItem createTestOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
    }

    private Order createTestOrder() {
        return Order.builder()
                .account(account)
                .items(List.of(orderItem))
                .build();
    }

    private OrderItem createSecondTestOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();
    }

    private Order createSecondTestOrder() {
        return Order.builder()
                .account(account)
                .items(List.of(secondOrderItem))
                .build();
    }

    private void createRepository() {
        invoiceRepository = new FileInvoiceRepository(file, orderRepository);
    }
}
