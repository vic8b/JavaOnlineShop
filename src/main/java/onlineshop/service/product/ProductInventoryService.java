package onlineshop.service.product;

import lombok.NonNull;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.product.Product;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.repo.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

public class ProductInventoryService {
    private final ProductRepository productRepository;

    private final Object stockLock = new Object();

    public ProductInventoryService(@NonNull ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(@NonNull Product product) {
        productRepository.add(product);
    }

    public boolean deleteProduct(@NonNull String id) {
        return (productRepository.delete(id));
    }

    public void updateProduct(@NonNull String id, @NonNull Product product) {
        productRepository.updateProduct(id, product);
    }

    public void changePrice(@NonNull String id, @NonNull BigDecimal newPrice) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.changePrice(newPrice);
    }

    public Product findProductById(@NonNull String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void increaseStock(@NonNull String id, int amount) {
        synchronized (stockLock) {
            findProductById(id).increaseQuantity(amount);
        }
    }

    public void decreaseStock(@NonNull String id, int amount) {
        synchronized (stockLock) {
            Product product = findProductById(id);

            if (product.getQuantity() < amount) {
                throw new ProductUnavailableException(product.getId(), amount, product.getQuantity());
            }

            product.decreaseQuantity(amount);
        }
    }

    public List<Product> reserveStock(@NonNull List<CartItem> cartItems) {
        synchronized (stockLock) {
            List<Product> products = cartItems.stream()
                    .map(cartItem -> {
                        Product product = findProductById(cartItem.getProduct().getId());

                        if (product.getQuantity() < cartItem.getQuantity()) {
                            throw new ProductUnavailableException(
                                    product.getId(),
                                    cartItem.getQuantity(),
                                    product.getQuantity()
                            );
                        }

                        return product;
                    })
                    .toList();

            IntStream.range(0, cartItems.size())
                    .forEach(index -> products.get(index).decreaseQuantity(cartItems.get(index).getQuantity()));

            return products;
        }
    }
}
