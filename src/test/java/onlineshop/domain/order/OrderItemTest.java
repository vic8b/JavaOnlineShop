package onlineshop.domain.order;

import onlineshop.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class OrderItemTest {
    @Mock
    Product product;

    @Test
    void shouldThrowExceptionWhenQuantityIsNotPositive() {
        assertThatThrownBy(() -> OrderItem.builder()
                .product(product)
                .quantity(-1)
                .unitPrice(BigDecimal.TEN)
                .build())
                .hasMessage("Quantity must be positive")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> OrderItem.builder()
                .product(product)
                .quantity(0)
                .unitPrice(BigDecimal.TEN)
                .build())
                .hasMessage("Quantity must be positive")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsNotPositive() {
        assertThatThrownBy(() -> OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("-1"))
                .build())
                .hasMessage("Unit price must be positive")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(BigDecimal.ZERO)
                .build())
                .hasMessage("Unit price must be positive")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCalculateTotalPrice() {
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();

        assertThat(orderItem.totalPrice())
                .isEqualByComparingTo("20");
    }
}
