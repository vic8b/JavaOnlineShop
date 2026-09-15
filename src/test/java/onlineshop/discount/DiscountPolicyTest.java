package onlineshop.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountPolicyTest {
    @Test
    void shouldCombineDiscounts() {
        //Arrange
        DiscountPolicy firstPolicy =
                (items, regularPrice) -> new BigDecimal("20.00");

        DiscountPolicy secondPolicy =
                (items, regularPrice) -> new BigDecimal("10.00");

        DiscountPolicy combinedPolicies = firstPolicy.andThen(secondPolicy);

        //Act
        BigDecimal discount = combinedPolicies.calculateDiscount(
                List.of(),
                new BigDecimal("100.00")
        );

        //Assert
        assertThat(discount).isEqualByComparingTo("30.00");
    }

    @Test
    void shouldLimitCombinedDiscountToRegularPrice() {
        //Arrange
        DiscountPolicy firstPolicy =
                (items, regularPrice) -> new BigDecimal("100.00");

        DiscountPolicy secondPolicy =
                (items, regularPrice) -> new BigDecimal("50.00");

        DiscountPolicy combinedPolicies = firstPolicy.andThen(secondPolicy);

        //Act
        BigDecimal discount = combinedPolicies.calculateDiscount(
                List.of(),
                new BigDecimal("100.00")
        );

        //Assert
        assertThat(discount).isEqualByComparingTo("100.00");
    }

    @Test
    void shouldCalculateEachDiscountUsingOriginalPrice() {
        //Arrange
        BigDecimal regularPrice = new BigDecimal("100.00");

        DiscountPolicy firstPolicy =
                (items, price) -> price.multiply(new BigDecimal("0.10"));

        DiscountPolicy secondPolicy =
                (items, price) -> price.multiply(new BigDecimal("0.20"));

        DiscountPolicy combinedPolicy = firstPolicy.andThen(secondPolicy);

        //Act
        BigDecimal discount = combinedPolicy.calculateDiscount(List.of(), regularPrice);

        //Assert
        assertThat(discount).isEqualByComparingTo("30.00");
    }
}
