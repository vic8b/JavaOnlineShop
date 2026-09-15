package onlineshop.repo;

import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.order.OrderStatus;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.OrderAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class InMemoryOrderRepositoryTest {
    Order order;
    OrderRepository orderRepository;
    List<OrderItem> items;
    OrderItem orderItem;
    @Mock
    Product product;
    @Mock
    Account account;

    @BeforeEach
    void setup() {
        orderRepository = new InMemoryOrderRepository();
        items = new ArrayList<>();

        orderItem = createOrderItem();

        items.add(orderItem);

        order = createOrder();
    }

    @Test
    void shouldAddOrderToTheRepository() {
        orderRepository.add(order);

        assertThat(orderRepository.findAll())
                .containsExactly(order);

        assertThat(orderRepository.findById(order.getOrderId()))
                .isPresent()
                .contains(order);
    }

    @Test
    void shouldRejectDuplicateOrderId() {
        UUID duplicatedId = UUID.randomUUID();

        Order originalOrder = createOrderWithId(duplicatedId);
        Order orderDuplicate = createOrderWithId(duplicatedId);

        orderRepository.add(originalOrder);

        assertThatThrownBy(() -> orderRepository.add(orderDuplicate))
                .isInstanceOf(OrderAlreadyExistsException.class)
                .hasMessage("Order with id " + orderDuplicate.getOrderId() + " already exists");

        assertThat(orderRepository.findById(duplicatedId))
                .contains(originalOrder);
    }

    @Test
    void shouldFindOrderById() {
        orderRepository.add(order);

        assertThat(orderRepository.findById(order.getOrderId()))
                .contains(order);
    }

    @Test
    void shouldFindAllOrders() {
        Order secondOrder = Order.builder()
                .account(account)
                .items(items)
                .build();

        orderRepository.add(order);
        orderRepository.add(secondOrder);

        assertThat(orderRepository.findAll())
                .contains(order)
                .contains(secondOrder);
    }

    private Order createOrder() {
        return Order.builder()
                .account(account)
                .items(items)
                .build();
    }

    private Order createOrderWithId(UUID orderId) {
        return new Order(
                orderId,
                account,
                items,
                LocalDateTime.now(),
                OrderStatus.PENDING
        );
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
    }

    @Test
    void shouldReturnEmptyWhenOrderDoesNotExist() {
        UUID unknownOrderId = UUID.randomUUID();

        assertThat(orderRepository.findById(unknownOrderId))
                .isEmpty();
    }
}
