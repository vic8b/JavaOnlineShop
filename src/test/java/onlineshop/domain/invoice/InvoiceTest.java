package onlineshop.domain.invoice;

import onlineshop.domain.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InvoiceTest {
    @Mock
    Order order;

    @Test
    void shouldCreateInvoice() {
        Invoice invoice = Invoice.builder()
                .invoiceId("INV-1")
                .order(order)
                .build();

        assertThat(invoice.getInvoiceId()).isEqualTo("INV-1");
        assertThat(invoice.getOrder()).isSameAs(order);
        assertNotNull(invoice.getIssueDate());
    }

    @Test
    void shouldBlankInvoiceIdThrowException() {
        assertThatThrownBy(() -> Invoice.builder()
                .invoiceId("")
                .order(order)
                .build())
                .hasMessage("ID cannot be blank")
                .isInstanceOf(IllegalArgumentException.class);
    }
}
