package onlineshop.service.invoice;

import lombok.NonNull;
import onlineshop.domain.invoice.Invoice;

import java.time.Clock;
import java.time.Year;
import java.util.Collection;

public class SequentialInvoiceNumberGenerator implements InvoiceNumberGenerator {
    private static final String PREFIX = "INV-";

    private final int year;
    private int counter;

    public SequentialInvoiceNumberGenerator(
            @NonNull Collection<Invoice> existingInvoices,
            @NonNull Clock clock
            ) {
        this.year = Year.now(clock).getValue();
        this.counter = determineNextCounter(existingInvoices);
    }

    @Override
    public String generate() {
        return PREFIX + year + "/" + counter++;
    }

    private int determineNextCounter(@NonNull Collection<Invoice> existingInvoices) {
        return existingInvoices.stream()
                .map(Invoice::getInvoiceNumber)
                .mapToInt(this::extractCurrentOrInitialize)
                .max()
                .orElse(0) + 1;
    }

    private int extractCurrentOrInitialize(@NonNull String invoiceNumber) {
        String expectedPrefix = PREFIX + year + "/";

        if (!invoiceNumber.startsWith(expectedPrefix)) {
            return 0;
        }

        String sequence = invoiceNumber.substring(expectedPrefix.length());

        try {
            int sequenceInt = Integer.parseInt(sequence);

            return Math.max(sequenceInt, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
