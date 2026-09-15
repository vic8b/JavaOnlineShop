package onlineshop.repo;

import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.OrderAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    void shouldAddPutAnOrderToTheRepository() {
        orderRepository.add(order);

        assertThat(orderRepository.findAll())
                .singleElement()
                .isEqualTo(order);

        assertThat(orderRepository.findById("ORD-1"))
                .isPresent()
                .contains(order);
    }

    @Test
    void shouldRejectDuplicateOrderId() {
        Order orderDuplicate = createOrder();

        orderRepository.add(order);

        assertThatThrownBy(() -> orderRepository.add(orderDuplicate))
                .isInstanceOf(OrderAlreadyExistsException.class)
                .hasMessage("Order with id " + orderDuplicate.getOrderId() + " already exists");

        assertThat(orderRepository.findById(orderDuplicate.getOrderId()))
                .contains(order);
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
                .orderId("ORD-2")
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
                .orderId("ORD-1")
                .account(account)
                .items(items)
                .build();
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        assertThat(orderRepository.findById("UNKNOWN"))
                .isEmpty();
    }
}
