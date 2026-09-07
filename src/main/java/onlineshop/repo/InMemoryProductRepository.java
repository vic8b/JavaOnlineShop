package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.product.Product;
import onlineshop.exception.ProductAlreadyExistsException;
import onlineshop.exception.ProductNotFoundException;

import java.util.*;

@Slf4j
public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> productsRepo = new HashMap<>();

    @Override
    public void add(@NonNull Product product) {
        addIfAbsent(product);

        log.info("Product {} has been added to the repository", product);
    }

    @Override
    public boolean delete(@NonNull String id) {
        return (productsRepo.remove(id)) != null;
    }

    @Override
    public void updateProduct(@NonNull String id, @NonNull Product product) {
        if (!product.getId().equals(id)) throw new IllegalArgumentException("Product ids do not match");

        if (productsRepo.replace(id, product) == null) {
            throw new ProductNotFoundException(id);
        }
    }

    @Override
    public Optional<Product> findById(@NonNull String id) {
        return Optional.ofNullable(productsRepo.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(productsRepo.values());
    }

    private void addIfAbsent(Product product) {
        if (productsRepo.putIfAbsent(product.getId(), product) != null) {
            throw new ProductAlreadyExistsException(product.getId());
        }
    }
}
