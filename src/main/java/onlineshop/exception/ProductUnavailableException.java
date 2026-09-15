package onlineshop.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(String productId, int requestedQuantity, int availableQuantity) {
        super("Product " + productId + " is unavailable in requested quantity." + "\nRequested: " + requestedQuantity
        + "\nAvailable: " + availableQuantity);
    }
}
