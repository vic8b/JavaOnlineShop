package onlineshop.service.order;

import lombok.NonNull;
import onlineshop.domain.order.Order;
import onlineshop.repo.OrderRepository;

import java.util.List;

public class OrderQueryService {
    private final OrderRepository orderRepository;

    public OrderQueryService(@NonNull OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findOrdersForAccount(@NonNull String accountId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getAccount().getAccountId().equals(accountId))
                .toList();
    }
}
