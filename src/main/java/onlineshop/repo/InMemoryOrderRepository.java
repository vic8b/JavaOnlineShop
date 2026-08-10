package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.order.Order;
import onlineshop.exception.OrderAlreadyExistsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orderRepo = new ConcurrentHashMap<>();

    @Override
    public synchronized void add(@NonNull Order order) {
        validateIfOrderAlreadyExists(order);

        log.info("Order: {} has been added to the repository", order.getOrderId());
    }

    private void validateIfOrderAlreadyExists(Order order) {
        if (orderRepo.putIfAbsent(order.getOrderId(), order) != null) {
            throw new OrderAlreadyExistsException(order.getOrderId().toString());
        }
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
