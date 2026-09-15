package onlineshop.exception;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException(String id) {
        super("Order with id " + id + " already exists");
    }
}
