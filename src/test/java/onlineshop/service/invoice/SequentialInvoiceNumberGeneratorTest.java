package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SequentialInvoiceNumberGeneratorTest {
    @Test
    void shouldStartFromOneWhenNoInvoicesExist() {
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of());

        assertThat(generator.generate()).isEqualTo("INV-" + Year.now().getValue() + "/1");
    }

    @Test
    void shouldContinueFromHighestExistingNumber() {
        Invoice first = mock(Invoice.class);
        Invoice second = mock(Invoice.class);

        int year = Year.now().getValue();

        when(first.getInvoiceNumber())
                .thenReturn("INV-" + year + "/2");

        when(second.getInvoiceNumber())
                .thenReturn("INV-" + year + "/7");

        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(first, second));

        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/8");
    }

    @Test
    void shouldIgnoreInvoicesFromPreviousYear() {
        Invoice invoice = mock(Invoice.class);

        int currentYear = Year.now().getValue();

        when(invoice.getInvoiceNumber())
                .thenReturn("INV-" + (currentYear - 1) + "/100");

        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(invoice));

        assertThat(generator.generate())
                .isEqualTo("INV-" + currentYear + "/1");
    }

    @Test
    void shouldGenerateSequentialNumbers() {
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of());

        int year = Year.now().getValue();

        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/1");

        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/2");
    }
}