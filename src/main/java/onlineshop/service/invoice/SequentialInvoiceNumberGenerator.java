package onlineshop.service.invoice;

import java.time.LocalDateTime;

public class SequentialInvoiceNumberGenerator implements InvoiceNumberGenerator {
    private int counter = 1;

    @Override
    public String generate() {
        return "INV-" + LocalDateTime.now().getYear() + "/" + counter++;
    }
}
