package onlineshop.exception;

public class OrderPersistenceException extends RuntimeException {
    public OrderPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderPersistenceException(String message) {
        super(message);
    }
}
