package onlineshop.repo;

import onlineshop.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    //TODO cancel order possibility in the future
    // + to validate if deleting an order should correct invoice

    void add(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
}
