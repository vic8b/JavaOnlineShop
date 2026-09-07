package onlineshop.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoDiscountPolicyTest {
    @Test
    void shouldNotApplyDiscount() {
        // Arrange
        DiscountPolicy discountPolicy = new NoDiscountPolicy();

        //Act
        BigDecimal discount = discountPolicy.calculateDiscount(List.of(), new BigDecimal("100.00"));

        //Assert
        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
