package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    //TODO cancel order possibility in the future
    // + to validate if deleting an order should correct invoice

    void add(@NonNull Order order);
    Optional<Order> findById(@NonNull UUID orderId);
    List<Order> findAll();
}
