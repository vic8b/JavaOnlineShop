package onlineshop.domain.invoice;

import onlineshop.domain.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class InvoiceTest {
    @Mock
    Order order;

    @Test
    void shouldCreateInvoice() {
        //Arrange
        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-1")
                .order(order)
                .issueDate(Instant.now())
                .build();

        //Act + Assert
        assertThat(invoice.getInvoiceNumber()).isEqualTo("INV-1");
        assertThat(invoice.getOrder()).isSameAs(order);
        assertThat(invoice.getIssueDate()).isNotNull();
    }

    @Test
    void shouldBlankInvoiceNumberThrowException() {
        //Act + Assert
        assertThatThrownBy(() -> Invoice.builder()
                .invoiceNumber("")
                .order(order)
                .issueDate(Instant.now())
                .build())
                .hasMessage("Invoice number cannot be blank")
                .isInstanceOf(IllegalArgumentException.class);
    }
}
