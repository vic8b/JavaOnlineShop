package onlineshop.repo;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.exception.InvoiceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryInvoiceRepositoryTest {
    @Mock
    Order order;
    @Mock
    Order secondOrder;

    Invoice invoice;
    InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        invoiceRepository = new InMemoryInvoiceRepository();

        invoice = Invoice.builder()
                .invoiceNumber("INV-2026/1")
                .order(order)
                .issueDate(Instant.now())
                .build();
    }

    @Test
    void shouldAddInvoiceToTheRepository() {
        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findAll())
                .singleElement()
                .isEqualTo(invoice);

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .isPresent()
                .contains(invoice);
    }

    @Test
    void shouldRejectDuplicateInvoiceNumber() {
        Invoice invoiceDuplicate = Invoice.builder()
                .invoiceNumber("INV-2026/1")
                .order(order)
                .issueDate(Instant.now())
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
        UUID mockId = UUID.randomUUID();

        Invoice originalInvoice = new Invoice(mockId, "INV-2026/1", order, Instant.now());
        Invoice duplicateTestInvoice = new Invoice(mockId, "INV-2026/2", order, Instant.now());

        invoiceRepository.add(originalInvoice);

        assertThatThrownBy(() -> invoiceRepository.add(duplicateTestInvoice))
                .isInstanceOf(InvoiceAlreadyExistsException.class)
                .hasMessage("Invoice with id " + mockId + " already exists");

        assertThat(invoiceRepository.findById(mockId))
                .contains(originalInvoice);
    }

    @Test
    void shouldFindInvoiceById() {
        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .contains(invoice);
    }

    @Test
    void shouldFindInvoiceByNumber() {
        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findByNumber(invoice.getInvoiceNumber()))
                .contains(invoice);
    }

    @Test
    void shouldFindCorrectInvoiceByOrderId() {
        UUID mockId = UUID.randomUUID();
        when(order.getOrderId()).thenReturn(mockId);

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findByOrderId(mockId))
                .contains(invoice);
    }

    @Test
    void shouldFindAllInvoices() {
        Invoice secondInvoice = Invoice.builder()
                .invoiceNumber("INV-2026/2")
                .order(secondOrder)
                .issueDate(Instant.now())
                .build();

        invoiceRepository.add(invoice);
        invoiceRepository.add(secondInvoice);

        assertThat(invoiceRepository.findAll())
                .contains(invoice)
                .contains(secondInvoice);
    }

    @Test
    void shouldReturnEmptyWhenInvoiceDoesNotExist() {
        assertThat(invoiceRepository.findById(UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenInvoiceForOrderDoesNotExist() {
        assertThat(invoiceRepository.findByOrderId(UUID.randomUUID()))
                .isEmpty();
    }
}
