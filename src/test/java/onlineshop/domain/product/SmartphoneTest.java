package onlineshop.domain.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;

class SmartphoneTest {
    @Test
    void shouldBuilderCreateInstance() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .build();

        assertThat(testSmartphone)
                .hasFieldOrPropertyWithValue("id", "1")
                .hasFieldOrPropertyWithValue("name", "testSmartphone")
                .hasFieldOrPropertyWithValue("price", new BigDecimal("10").setScale(2, RoundingMode.HALF_UP))
                .hasFieldOrPropertyWithValue("quantity", 1)
                .hasFieldOrPropertyWithValue("color", "yellow")
                .hasFieldOrPropertyWithValue("batteryCapacity", "1050 mAh");
    }

    @Test
    void shouldLombokGetterWorkSuccessfully() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .build();

        assertThat(testSmartphone.getName())
                .isEqualTo("testSmartphone");
    }

    @Test
    void shouldNegativePriceThrowException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Smartphone.builder()
                        .id("1")
                        .name("testSmartphone")
                        .price(new BigDecimal("-10"))
                        .quantity(1)
                        .color("yellow")
                        .batteryCapacity("1050 mAh")
                        .accessory("case")
                        .accessory("charger")
                        .build());

        assertThatThrownBy(() -> Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("-10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build())
                .hasMessage("Price must be positive");
    }

    @Test
    void shouldNegativeQuantityThrowException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        Smartphone.builder()
                                .id("1")
                                .name("testSmartphone")
                                .price(new BigDecimal("10"))
                                .quantity(-2)
                                .color("yellow")
                                .batteryCapacity("1050 mAh")
                                .accessory("case")
                                .accessory("charger")
                                .build());

        assertThatThrownBy(() -> Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(-2)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build())
                .hasMessage("Quantity cannot be negative");
    }

    @Test
    void shouldBuilderCreateListOfAccessoriesSuccessfully() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        assertThat(testSmartphone.getAccessories())
                .contains("case")
                .contains("charger");
    }

    @Test
    void shouldEqualsAndHashCodeCompareObjectsSuccessfully() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        Smartphone testSmartphone2 = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        assertThat(testSmartphone)
                .isEqualTo(testSmartphone2);
    }

    @Test
    void shouldSuccessfullyIncreaseQuantity() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        testSmartphone.increaseQuantity(1);

        assertThat(testSmartphone.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void shouldSuccessfullyDecreaseQuantity() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        testSmartphone.decreaseQuantity(1);

        assertThat(testSmartphone.getQuantity())
                .isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testSmartphone.increaseQuantity(-1));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testSmartphone.decreaseQuantity(-1));
    }

    @Test
    void shouldThrowExceptionWhenDecreasingAmountExceedsQuantityOfProduct() {
        Smartphone testSmartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("case")
                .accessory("charger")
                .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> testSmartphone.decreaseQuantity(-10));
    }
}
