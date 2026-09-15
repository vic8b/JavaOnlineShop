package onlineshop.exception;

public class InvoicePersistenceException extends RuntimeException {
    public InvoicePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvoicePersistenceException(String message) {
        super(message);
    }
}
