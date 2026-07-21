package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    Order order;

    @Test
    void shouldSuccessfullyGenerateInvoice() {
        Invoice invoice = new InvoiceService().generate(order);

        assertThat(invoice.getIssueDate())
                .isNotNull();

        assertThat(invoice)
                .hasFieldOrPropertyWithValue("order", order);

        assertThat(invoice.getInvoiceId())
                .startsWith("INV-" + invoice.getIssueDate().getYear() + "/");
    }
}
