package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.order.Order;
import onlineshop.exception.OrderAlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orderRepo = new HashMap<>();

    @Override
    public void add(@NonNull Order order) {
        if (orderRepo.putIfAbsent(order.getOrderId(), order) != null) {
            throw new OrderAlreadyExistsException(order.getOrderId());
        }

        System.out.println("Order: " + order.getOrderId() + " has been added to the repository");
    }

    @Override
    public Optional<Order> findById(@NonNull String id) {
        return Optional.ofNullable(orderRepo.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orderRepo.values());
    }
}
