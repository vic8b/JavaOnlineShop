package onlineshop.repo;

import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.order.OrderStatus;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.OrderAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FileOrderRepositoryTest {
    @TempDir
    Path tempDir;
    Path file;
    Order order;
    OrderRepository orderRepository;
    OrderRepository reader;
    List<OrderItem> items;
    OrderItem orderItem;
    Product product;
    Account account;

    @BeforeEach
    void setup() {
        file = tempDir.resolve("orders.txt");

        items = new ArrayList<>();

        product = Electronics.builder()
                .id("E-1")
                .name("Test electronics")
                .price(BigDecimal.TEN)
                .quantity(5)
                .build();

        account = Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();


        orderItem = createOrderItem();

        items.add(orderItem);

        order = createOrder();
    }

    @Test
    void shouldCreateFilesWhenItDoesNotExist() {
        Path file = tempDir.resolve("orders.txt");

        assertThat(file).doesNotExist();

        new FileOrderRepository(file);

        assertThat(file)
                .exists()
                .isRegularFile();
    }

    @Test
    void shouldAddOrderToRepository() {
        createRepository();

        orderRepository.add(order);

        assertThat(orderRepository.findAll())
                .containsExactly(order);

        assertThat(orderRepository.findById(order.getOrderId()))
                .isPresent()
                .contains(order);
    }

    @Test
    void shouldLoadPreviouslySavedOrder() {
        createRepository();

        orderRepository.add(order);

        reader = new FileOrderRepository(file);

        assertThat(reader.findById(order.getOrderId())).contains(order);
    }

    @Test
    void shouldLoadAllPreviouslySavedOrders() {
        createRepository();

        Order secondOrder = createOrderWithId(UUID.randomUUID());

        orderRepository.add(order);
        orderRepository.add(secondOrder);

        reader = new FileOrderRepository(file);

        assertThat(reader.findAll())
                .contains(order)
                .contains(secondOrder);
    }

    @Test
    void shouldRejectDuplicateOrderId () {
        createRepository();

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
    void shouldRejectDuplicateOrderIdsInFile() {
        createRepository();

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

    private void createRepository() {
        orderRepository = new FileOrderRepository(file);
    }

    private Order createOrder() {
        return Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .build();
    }

    private Order createOrderWithId(UUID orderId) {
        return new Order(
                orderId,
                account,
                items,
                Instant.now(),
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
}
