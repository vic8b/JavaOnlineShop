package onlineshop.exception;

import java.util.UUID;

public class InvoiceAlreadyExistsException extends RuntimeException {
    private InvoiceAlreadyExistsException(String message) {
        super(message);
    }

    public static InvoiceAlreadyExistsException forId(UUID invoiceId) {
        return new InvoiceAlreadyExistsException("Invoice with id " + invoiceId + " already exists");
    }

    public static InvoiceAlreadyExistsException forNumber(String invoiceNumber) {
        return new InvoiceAlreadyExistsException("Invoice with number " + invoiceNumber + " already exists");
    }
}
