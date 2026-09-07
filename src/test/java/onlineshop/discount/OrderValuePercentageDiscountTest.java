package onlineshop.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderValuePercentageDiscountTest {
    @Test
    void shouldApplyDiscountWhenMinimumValueIsReached() {
        //Arrange
        DiscountPolicy discountPolicy =
                new OrderValuePercentageDiscount(
                        new BigDecimal("100.00"), new BigDecimal("10")
                );

        //Act
        BigDecimal discount = discountPolicy.calculateDiscount(List.of(), new BigDecimal("200.00"));

        //Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void shouldNotApplyDiscountWhenBelowMinimumValue() {
        //Arrange
        DiscountPolicy discountPolicy =
                new OrderValuePercentageDiscount(
                        new BigDecimal("100.00"), new BigDecimal("10")
                );

        //Act
        BigDecimal discount = discountPolicy.calculateDiscount(List.of(), new BigDecimal("50.00"));

        //Assert
        assertThat(discount).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    void shouldThrowExceptionWhenDiscountPercentageIsIncorrect() {
        //Act + Assert
        assertThatThrownBy(
                () -> new OrderValuePercentageDiscount(new BigDecimal("10.00"), new BigDecimal("0"))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Percentage must be between 0 and 100");
    }

    @Test
    void shouldThrowExceptionWhenMinimumValueIsBelowZero() {
        //Act + Assert
        assertThatThrownBy(
                () -> new OrderValuePercentageDiscount(new BigDecimal("-1.00"), new BigDecimal("10"))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minimum value cannot be negative");
    }
}
