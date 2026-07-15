package onlineshop.repo;

import onlineshop.domain.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void add(Product product);
    boolean delete(String id);
    void changeSpecification(String id, Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
}
