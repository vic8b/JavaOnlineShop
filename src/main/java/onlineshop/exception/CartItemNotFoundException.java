package onlineshop.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String id) {
        super("Card Item with id " + id + " not found in the cart");
    }
}
