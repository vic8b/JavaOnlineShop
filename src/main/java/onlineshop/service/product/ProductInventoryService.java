package onlineshop.service.product;

import lombok.NonNull;
import onlineshop.domain.product.Product;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.repo.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProductInventoryService {
    private final ProductRepository productRepository;

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
        productRepository.changeSpecification(id, product);
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
        findProductById(id).increaseQuantity(amount);
    }

    public void decreaseStock(@NonNull String id, int amount) {
        Product product = findProductById(id);

        if (product.getQuantity() < amount) throw new ProductUnavailableException(
                product.getId(), amount, product.getQuantity());

        product.decreaseQuantity(amount);
    }
}
