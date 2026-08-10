package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    Order order;

    @Mock
    InvoiceNumberGenerator invoiceNumberGenerator;

    private static final Instant FIXED_INSTANT = Instant.parse("2026-08-02T12:00:00Z");

    private static final Clock CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    @Test
    void shouldSuccessfullyGenerateInvoice() {
        when(invoiceNumberGenerator.generate())
                .thenReturn("INV-2026/1");

        Invoice invoice = new InvoiceService(invoiceNumberGenerator, CLOCK).generate(order);

        assertThat(invoice.getIssueDate())
                .isNotNull()
                .isEqualTo(FIXED_INSTANT);

        assertThat(invoice)
                .hasFieldOrPropertyWithValue("order", order);

        assertThat(invoice.getInvoiceNumber())
                .isEqualTo("INV-2026/1");
    }
}
