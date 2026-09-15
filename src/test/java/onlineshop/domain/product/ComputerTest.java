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
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Assert
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
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Assert
        assertThat(testComputer.getName())
                .isEqualTo("testComputer");
    }

    @Test
    void shouldNegativePriceThrowException() {
        //Act + Assert
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
        //Act + Assert
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
        //Arrange
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

        //Assert
        assertThat(testComputer)
                .isEqualTo(testComputer2);
    }

    @Test
    void shouldSuccessfullyIncreaseQuantity() {
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Act
        testComputer.increaseQuantity(1);

        //Assert
        assertThat(testComputer.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldSuccessfullyDecreaseQuantity() {
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Act
        testComputer.decreaseQuantity(1);

        //Assert
        assertThat(testComputer.getQuantity())
                .isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.increaseQuantity(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.decreaseQuantity(-1));
    }

    @Test
    void shouldThrowExceptionWhenDecreasingAmountExceedsQuantityOfProduct() {
        //Arrange
        Computer testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testComputer.decreaseQuantity(-10));
    }
}
