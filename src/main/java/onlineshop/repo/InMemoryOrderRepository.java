package onlineshop.repo;

import lombok.NonNull;
import onlineshop.domain.order.Order;
import onlineshop.exception.OrderAlreadyExistsException;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orderRepo = new HashMap<>();

    @Override
    public void add(@NonNull Order order) {
        if (orderRepo.putIfAbsent(order.getOrderId(), order) != null) {
            throw new OrderAlreadyExistsException(order.getOrderId().toString());
        }

        System.out.println("Order: " + order.getOrderId() + " has been added to the repository");
    }

    @Override
    public Optional<Order> findById(@NonNull UUID id) {
        return Optional.ofNullable(orderRepo.get(id));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orderRepo.values());
    }
}
