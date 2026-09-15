package onlineshop.exception;

public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(String id) {
        super("Invoice with id " + id + " already exists");
    }
}
