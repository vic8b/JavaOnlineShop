package onlineshop.repo;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.exception.InvoiceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryInvoiceRepositoryTest {
    @Mock
    Order order;
    @Mock
    Order order2;
    Invoice invoice;
    InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        invoiceRepository = new InMemoryInvoiceRepository();

        invoice = Invoice.builder()
                .invoiceId("INV-2026/1")
                .order(order)
                .build();
    }

    @Test
    void shouldAddPutAnInvoiceToTheRepository() {
        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findAll())
                .singleElement()
                .isEqualTo(invoice);

        assertThat(invoiceRepository.findById("INV-2026/1"))
                .isPresent()
                .contains(invoice);
    }

    @Test
    void shouldRejectDuplicateInvoiceId() {
        Invoice invoiceDuplicate = Invoice.builder()
                .invoiceId("INV-2026/1")
                .order(order)
                .build();

        invoiceRepository.add(invoice);

        assertThatThrownBy(() -> invoiceRepository.add(invoiceDuplicate))
                .isInstanceOf(InvoiceAlreadyExistsException.class)
                .hasMessage("Invoice with id " + invoice.getInvoiceId() + " already exists");

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .contains(invoice);
    }

    @Test
    void shouldFindInvoiceById() {
        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findById(invoice.getInvoiceId()))
                .contains(invoice);
    }

    @Test
    void shouldFindCorrectOrderById() {
        String mockId = "ORD-1";
        when(order.getOrderId()).thenReturn(mockId);

        invoiceRepository.add(invoice);

        assertThat(invoiceRepository.findByOrderId(mockId))
                .contains(invoice);
    }

    @Test
    void shouldFindAllInvoices() {
        Invoice secondInvoice = Invoice.builder()
                .invoiceId("INV-2026/2")
                .order(order2)
                .build();

        invoiceRepository.add(invoice);
        invoiceRepository.add(secondInvoice);

        assertThat(invoiceRepository.findAll())
                .contains(invoice)
                .contains(secondInvoice);
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        assertThat(invoiceRepository.findById("UNKNOWN"))
                .isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenInvoiceForOrderDoesNotExist() {
        assertThat(invoiceRepository.findByOrderId("UNKNOWN"))
                .isEmpty();
    }
}
