package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    Order order;

    @Mock
    InvoiceNumberGenerator invoiceNumberGenerator;

    @Test
    void shouldSuccessfullyGenerateInvoice() {
        when(invoiceNumberGenerator.generate())
                .thenReturn("INV-2026/1");

        Invoice invoice = new InvoiceService(invoiceNumberGenerator).generate(order);

        assertThat(invoice.getIssueDate())
                .isNotNull();

        assertThat(invoice)
                .hasFieldOrPropertyWithValue("order", order);

        assertThat(invoice.getInvoiceNumber())
                .isEqualTo("INV-2026/1");
    }
}
