package onlineshop.domain.order;

import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    Account account;

    @Mock
    Product product;

    @Test
    void shouldConstructorSuccessfullyCreateOrderDate() {
        //Arrange
        OrderItem orderItem = createOrderItem();

        List<OrderItem> items = List.of(orderItem);

        Order order = Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .totalPrice(new BigDecimal("100.00"))
                .build();

        //Act + Assert
        assertNotNull(order.getOrderDate());
    }

    @Test
    void shouldConstructorSetOrderStatusToPending() {
        //Arrange
        OrderItem orderItem = createOrderItem();

        List<OrderItem> items = List.of(orderItem);

        //Act
        Order order = Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .totalPrice(new BigDecimal("100.00"))
                .build();

        //Assert
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldEmptyOrderItemsListThrowException() {
        //Arrange
        List<OrderItem> items = new ArrayList<>();

        //Act + Assert
        assertThatThrownBy(() -> Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .totalPrice(new BigDecimal("100.00"))
                .build())
                .hasMessage("Order must contain at least one item")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSuccessfullyCalculateTotalOrderPrice() {
        //Arrange
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

        //Act
        Order order = Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .totalPrice(new BigDecimal("130.00"))
                .build();

        //Assert
        assertThat(order.getTotalPrice())
                .isEqualByComparingTo("130.00");
    }

    @Test
    void shouldThrowExceptionWhenTotalPriceIsNegative() {
        //Arrange
        OrderItem orderItem = createOrderItem();

        List<OrderItem> items = List.of(orderItem);

        //Act + Assert
        assertThatThrownBy(() -> Order.builder()
                .account(account)
                .items(items)
                .orderDate(Instant.now())
                .totalPrice(new BigDecimal("-100.00"))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Total price cannot be negative");
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
    }
}
