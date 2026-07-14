package onlineshop.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;

class ElectronicsTest {
    @Test
    void shouldBuilderCreateInstance() {
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testElectronics")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        assertThat(testElectronics)
                .hasFieldOrPropertyWithValue("id", "1")
                .hasFieldOrPropertyWithValue("name", "testElectronics")
                .hasFieldOrPropertyWithValue("price", new BigDecimal("10").setScale(2, RoundingMode.HALF_UP))
                .hasFieldOrPropertyWithValue("quantity", 1);
    }

    @Test
    void shouldLombokGetterWorkSuccessfully() {
        Electronics testElectronics = Electronics.builder()
                .id("1")
                .name("testElectronics")
                .price(new BigDecimal("10"))
                .quantity(1)
                .build();

        assertThat(testElectronics.getName())
                .isEqualTo("testElectronics");
    }

    @Test
    void shouldNegativePriceThrowException() {
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
                .hasMessage("Price cannot be negative");
    }

    @Test
    void shouldNegativeQuantityThrowException() {
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

        assertThat(testElectronics)
                .isEqualTo(testElectronics2);
    }
}
