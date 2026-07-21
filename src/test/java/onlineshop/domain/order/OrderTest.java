package onlineshop.domain.order;

import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    Account account;

    @Mock
    List<OrderItem> items;

    @Mock
    Product product;

    @Test
    void shouldConstructorSuccessfullyCreateIssueDate() {
        OrderItem orderItem = createOrderItem();

        List<OrderItem> items = List.of(orderItem);

        Order order = Order.builder()
                .orderId("1")
                .account(account)
                .items(items)
                .build();

        assertNotNull(order.getOrderDate());
    }

    @Test
    void shouldConstructorSetOrderStatusToPending() {
        OrderItem orderItem = createOrderItem();

        List<OrderItem> items = List.of(orderItem);

        Order order = Order.builder()
                .orderId("1")
                .account(account)
                .items(items)
                .build();

        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldBlankOrderIdThrowException() {
        assertThatThrownBy(() -> Order.builder()
                .orderId("")
                .account(account)
                .items(items)
                .build())
                .hasMessage("ID cannot be blank")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldEmptyOrderItemsListThrowException() {
        List<OrderItem> items = new ArrayList<>();

        assertThatThrownBy(() -> Order.builder()
                .orderId("1")
                .account(account)
                .items(items)
                .build())
                .hasMessage("Order must contain at least one item")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSuccessfullyCalculateTotalOrderPrice() {
        OrderItem testOrderItem1 = OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("10"))
                .build();

        OrderItem testOrderItem2 = OrderItem.builder()
                .product(product)
                .quantity(3)
                .unitPrice(new BigDecimal("40"))
                .build();

        List<OrderItem> items = List.of(testOrderItem1, testOrderItem2);

        Order order = Order.builder()
                .orderId("1")
                .account(account)
                .items(items)
                .build();

        assertThat(order.getTotalPrice())
                .isEqualByComparingTo("130.00");
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
    }
}
