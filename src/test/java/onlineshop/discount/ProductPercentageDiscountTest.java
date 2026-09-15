package onlineshop.discount;

import onlineshop.domain.order.OrderItem;
import onlineshop.domain.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductPercentageDiscountTest {
    @Test
    void shouldCalculateDiscountForSelectedProduct() {
        Product product = mock(Product.class);
        OrderItem orderItem = mock(OrderItem.class);

        String testProductId = "P-001";
        BigDecimal regularPrice = new BigDecimal("100.00");

        when(product.getId()).thenReturn(testProductId);
        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getTotalPrice()).thenReturn(regularPrice);

        DiscountPolicy discountPolicy =
                new ProductPercentageDiscount(testProductId, new BigDecimal("10"));

        BigDecimal discount = discountPolicy.calculateDiscount(List.of(orderItem), regularPrice);

        assertThat(discount).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldReturnZeroDiscountWhenProductDoesNotMatch() {
        Product product = mock(Product.class);
        OrderItem orderItem = mock(OrderItem.class);

        BigDecimal regularPrice = new BigDecimal("100.00");

        when(product.getId()).thenReturn("P-002");
        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getTotalPrice()).thenReturn(regularPrice);

        DiscountPolicy discountPolicy =
                new ProductPercentageDiscount("P-001", new BigDecimal("10"));

        BigDecimal discount = discountPolicy.calculateDiscount(List.of(orderItem), regularPrice);

        assertThat(discount).isEqualByComparingTo("0.00");
    }
}
