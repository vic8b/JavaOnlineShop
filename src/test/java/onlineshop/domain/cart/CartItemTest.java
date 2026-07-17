package onlineshop.domain.cart;

import onlineshop.domain.product.Computer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartItemTest {
    Computer computer;

    @BeforeEach
    void testProductSetup() {
        computer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(10)
                .cpu("Intel")
                .ram("64 GB")
                .build();
    }

    @Test
    void shouldBuilderCreateInstance() {
        CartItem cartItem = CartItem.builder()
                .product(computer)
                .quantity(1)
                .build();

        assertThat(cartItem)
                .hasFieldOrPropertyWithValue("product", computer)
                .hasFieldOrPropertyWithValue("quantity", 1);
    }

    @Test
    void shouldNegativeQuantityThrowException() {
        assertThatThrownBy(() -> CartItem.builder()
                .product(computer)
                .quantity(-1)
                .build())
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldExceedingCartItemQuantityThrowException() {
        assertThatThrownBy(() -> CartItem.builder()
                .product(computer)
                .quantity(100)
                .build())
                .hasMessage("Required quantity exceeds item availability");
    }

    @Test
    void shouldSuccessfullyChangeQuantity() {
        CartItem cartItem = CartItem.builder()
                .product(computer)
                .quantity(1)
                .build();

        cartItem.changeQuantity(2);

        assertThat(cartItem.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldNegativeNewQuantityThrowException() {
        CartItem cartItem = CartItem.builder()
                .product(computer)
                .quantity(1)
                .build();

        assertThatThrownBy(() -> cartItem.changeQuantity(-2))
                .hasMessage("Quantity must be positive");
    }

    @Test
    void shouldExceedingNewQuantityThrowException() {
        CartItem cartItem = CartItem.builder()
                .product(computer)
                .quantity(1)
                .build();

        assertThatThrownBy(() -> cartItem.changeQuantity(1000))
                .hasMessage("Required quantity exceeds item availability");
    }
}
