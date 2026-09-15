package onlineshop.domain.cart;

import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Electronics;
import onlineshop.exception.CartItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class CartTest {
    Cart cart;
    Computer computer;

    @BeforeEach
    void setup() {
        cart = new Cart("Account");

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
    void shouldAddNewProductToTheCart() {
        cart.addProduct(computer, 1);

        assertThat(cart.getItems())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.getProduct()).isEqualTo(computer);
                    assertThat(item.getQuantity()).isEqualTo(1);
                });
    }

    @Test
    void shouldChangeQuantityWhenProductIsAlreadyInTheCart() {
        cart.addProduct(computer, 1);
        CartItem cartItem = cart.getItems().getFirst();

        assertThat(cartItem.getQuantity())
                .isEqualTo(1);

        cart.addProduct(computer, 1);
        assertThat(cartItem.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenTotalAddedQuantityExceedsAvailability() {
        cart.addProduct(computer, 8);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> cart.addProduct(computer, 3));

        assertThat(cart.getItems())
                .singleElement()
                .extracting(CartItem::getQuantity)
                .isEqualTo(8);
    }

    @Test
    void shouldThrowNPEWhenProductIsNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> cart.addProduct(null, 1));
    }

    @Test
    void shouldSuccessfullyRemoveProduct() {
        cart.addProduct(computer, 1);

        boolean result = cart.removeProduct("1");

        assertThat(result)
                .isTrue();
        assertThat(cart.getItems())
                .isEmpty();
    }

    @Test
    void shouldReturnFalseWhenProductDoesNotExistInTheCart() {
        boolean result = cart.removeProduct("1");

        assertThat(result)
                .isFalse();
    }

    @Test
    void shouldChangeCartItemQuantity() {
        cart.addProduct(computer, 1);
        CartItem cartItem = cart.getItems().getFirst();
        cart.changeQuantity("1", 10);

        assertThat(cartItem.getQuantity())
                .isEqualTo(10);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExistInTheCart() {
        assertThatExceptionOfType(CartItemNotFoundException.class)
                .isThrownBy(() -> cart.changeQuantity("1", 10));
    }

    @Test
    void shouldGetAllTheItemsFromTheCart() {
        Electronics testElectronics = Electronics.builder()
                .id("2")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        cart.addProduct(computer, 1);
        cart.addProduct(testElectronics, 1);

        assertThat(cart.getItems())
                .hasSize(2)
                .extracting(item -> item.getProduct().getId())
                .containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void shouldReturnTrueWhenCartIsEmpty() {
        assertThat(cart.isEmpty())
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenCartIsNotEmpty() {
        cart.addProduct(computer, 1);

        assertThat(cart.isEmpty())
                .isFalse();
    }

    @Test
    void shouldClearCart() {
        cart.addProduct(computer, 1);

        assertThat(cart.isEmpty())
                .isFalse();

        cart.clear();

        assertThat(cart.isEmpty())
                .isTrue();
    }
}
