package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.product.Product;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> productsRepo = new HashMap<>();

    @Override
    public void add(@NonNull Product product) {
        if (productsRepo.putIfAbsent(product.getId(), product) != null) {
            throw new IllegalArgumentException("Product with id " + product.getId() + " already exists");
        }

        System.out.println("Product: " + product + " has been added to repository");
    }

    @Override
    public boolean delete(@NonNull String id) {
        return (productsRepo.remove(id)) != null;
    }

    @Override
    public void changeSpecification(@NonNull String id, @NonNull Product product) {
        if (!product.getId().equals(id)) throw new IllegalArgumentException("Product ids do not match");

        if (productsRepo.replace(id, product) == null) {
            throw new NoSuchElementException("Product with id " + id + " does not exist");
        }
    }

    @Override
    public Optional<Product> findById(@NonNull String id) {
        return Optional.ofNullable(productsRepo.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(productsRepo.values().stream().toList());
    }
}
