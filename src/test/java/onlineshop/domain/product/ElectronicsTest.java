package onlineshop.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;

class ElectronicsTest {
    @Test
    void shouldBuilderCreateInstance() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testElectronics")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Assert
        assertThat(testElectronics)
                .hasFieldOrPropertyWithValue("id", "1")
                .hasFieldOrPropertyWithValue("name", "testElectronics")
                .hasFieldOrPropertyWithValue("price", new BigDecimal("10").setScale(2, RoundingMode.HALF_UP))
                .hasFieldOrPropertyWithValue("quantity", 1);
    }

    @Test
    void shouldLombokGetterWorkSuccessfully() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testElectronics")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Assert
        assertThat(testElectronics.getName())
                .isEqualTo("testElectronics");
    }

    @Test
    void shouldNegativePriceThrowException() {
        //Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Electronics.builder()
                                .id("1")
                                .name("testSmartphone")
                                .price(new BigDecimal("-10"))
                                .quantity(1)
                                .build());

        assertThatThrownBy(() -> Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("-10"))
                .quantity(1)
                .build())
                .hasMessage("Price must be positive");
    }

    @Test
    void shouldNegativeQuantityThrowException() {
        //Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Electronics.builder()
                                .id("1")
                                .name("testSmartphone")
                                .price(new BigDecimal("10"))
                                .quantity(-2)
                                .build());

        assertThatThrownBy(() -> Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(-2)
                .build())
                .hasMessage("Quantity cannot be negative");
    }

    @Test
    void shouldEqualsAndHashCodeCompareObjectsSuccessfully() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        Electronics testElectronics2 = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Assert
        assertThat(testElectronics)
                .isEqualTo(testElectronics2);
    }

    @Test
    void shouldSuccessfullyIncreaseQuantity() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Act
        testElectronics.increaseQuantity(1);

        //Assert
        assertThat(testElectronics.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldSuccessfullyDecreaseQuantity() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Act
        testElectronics.decreaseQuantity(1);

        //Assert
        assertThat(testElectronics.getQuantity())
                .isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testElectronics.increaseQuantity(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testElectronics.decreaseQuantity(-1));
    }

    @Test
    void shouldThrowExceptionWhenDecreasingAmountExceedsQuantityOfProduct() {
        //Arrange
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        //Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testElectronics.decreaseQuantity(-10));
    }
}
