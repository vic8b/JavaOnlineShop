package onlineshop.service;

import lombok.NonNull;
import onlineshop.domain.product.Product;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.repo.ProductRepository;

import java.util.List;

public class ProductManager {
    private final ProductRepository productRepository;

    ProductManager(ProductRepository productRepository) {
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

    public Product findProductById(@NonNull String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void increaseQuantity(@NonNull String id, int amount) {
        findProductById(id).increaseQuantity(amount);
    }

    public void decreaseQuantity(@NonNull String id, int amount) {
        findProductById(id).decreaseQuantity(amount);
    }
}
