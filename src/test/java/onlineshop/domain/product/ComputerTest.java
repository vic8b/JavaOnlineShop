package onlineshop.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComputerTest {
    @Test
    void shouldBuilderCreateInstance() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        assertThat(testComputer)
                .hasFieldOrPropertyWithValue("id", "1")
                .hasFieldOrPropertyWithValue("name", "testComputer")
                .hasFieldOrPropertyWithValue("price", new BigDecimal("10").setScale(2, RoundingMode.HALF_UP))
                .hasFieldOrPropertyWithValue("quantity", 1)
                .hasFieldOrPropertyWithValue("cpu", "Intel")
                .hasFieldOrPropertyWithValue("ram", "64 GB");
    }

    @Test
    void shouldLombokGetterWorkSuccessfully() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        assertThat(testComputer.getName())
                .isEqualTo("testComputer");
    }

    @Test
    void shouldNegativePriceThrowException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Computer.builder()
                                .id("1")
                                .name("testComputer")
                                .price(new BigDecimal("-10"))
                                .quantity(1)
                                .cpu("Intel")
                                .ram("64 GB")
                                .build());

        assertThatThrownBy(() -> Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("-10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build())
                .hasMessage("Price must be positive");
    }

    @Test
    void shouldNegativeQuantityThrowException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Computer.builder()
                                .id("1")
                                .name("testComputer")
                                .price(new BigDecimal("10"))
                                .quantity(-1)
                                .cpu("Intel")
                                .ram("64 GB")
                                .build());

        assertThatThrownBy(() -> Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(-1)
                .cpu("Intel")
                .ram("64 GB")
                .build())
                .hasMessage("Quantity cannot be negative");
    }

    @Test
    void shouldEqualsAndHashCodeCompareObjectsSuccessfully() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        Computer testComputer2 = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        assertThat(testComputer)
                .isEqualTo(testComputer2);
    }

    @Test
    void shouldSuccessfullyIncreaseQuantity() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        testComputer.increaseQuantity(1);

        assertThat(testComputer.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldSuccessfullyDecreaseQuantity() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        testComputer.decreaseQuantity(1);

        assertThat(testComputer.getQuantity())
                .isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.increaseQuantity(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.decreaseQuantity(-1));
    }

    @Test
    void shouldThrowExceptionWhenDecreasingAmountExceedsQuantityOfProduct() {
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.decreaseQuantity(-10));
    }
}
