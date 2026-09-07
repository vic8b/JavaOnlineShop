package onlineshop.service.invoice;

import onlineshop.domain.invoice.Invoice;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SequentialInvoiceNumberGeneratorTest {
    private static final Instant FIXED_INSTANT = Instant.parse("2026-08-02T12:00:00Z");

    private static final Clock CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    @Test
    void shouldStartFromOneWhenNoInvoicesExist() {
        //Arrange
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(), CLOCK);

        //Assert
        assertThat(generator.generate()).isEqualTo("INV-" + Year.now(CLOCK).getValue() + "/1");
    }

    @Test
    void shouldContinueFromHighestExistingNumber() {
        //Arrange
        Invoice first = mock(Invoice.class);
        Invoice second = mock(Invoice.class);

        int year = Year.now(CLOCK).getValue();

        when(first.getInvoiceNumber())
                .thenReturn("INV-" + year + "/2");

        when(second.getInvoiceNumber())
                .thenReturn("INV-" + year + "/7");

        //Act
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(first, second), CLOCK);

        //Assert
        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/8");
    }

    @Test
    void shouldIgnoreInvoicesFromPreviousYear() {
        //Arrange
        Invoice invoice = mock(Invoice.class);

        int currentYear = Year.now(CLOCK).getValue();

        when(invoice.getInvoiceNumber())
                .thenReturn("INV-" + (currentYear - 1) + "/100");

        //Act
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(invoice), CLOCK);

        //Assert
        assertThat(generator.generate())
                .isEqualTo("INV-" + currentYear + "/1");
    }

    @Test
    void shouldGenerateSequentialNumbers() {
        //Arrange
        SequentialInvoiceNumberGenerator generator = new SequentialInvoiceNumberGenerator(List.of(), CLOCK);

        int year = Year.now(CLOCK).getValue();

        //Assert
        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/1");

        assertThat(generator.generate())
                .isEqualTo("INV-" + year + "/2");
    }

    @Test
    void shouldGenerateUniqueNumbersConcurrently() {
        //Arrange
        int numberOfTasks = 100;

        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {

            SequentialInvoiceNumberGenerator generator =
                    new SequentialInvoiceNumberGenerator(List.of(), CLOCK);

            try {
                //Act
                List<Callable<String>> tasks =
                        IntStream.range(0, numberOfTasks)
                                .mapToObj(i -> (Callable<String>) generator::generate)
                                .toList();

                List<String> numbers = executor.invokeAll(tasks).stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e.getCause());
                            }
                        })
                        .toList();

                //Assert
                assertThat(numbers)
                        .hasSize(numberOfTasks)
                        .doesNotHaveDuplicates();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}