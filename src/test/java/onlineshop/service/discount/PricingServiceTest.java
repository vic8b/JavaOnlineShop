package onlineshop.service.discount;

import onlineshop.discount.DiscountPolicy;
import onlineshop.domain.order.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {
    @Mock
    DiscountPolicy discountPolicy;

    @Test
    void shouldCalculateFinalPriceAfterDiscount() {
        //Arrange
        OrderItem firstOrderItem = mock(OrderItem.class);
        OrderItem secondOrderItem = mock(OrderItem.class);

        when(firstOrderItem.totalPrice()).thenReturn(new BigDecimal("10.00"));
        when(secondOrderItem.totalPrice()).thenReturn(new BigDecimal("100.00"));

        List<OrderItem> orderItems = List.of(firstOrderItem, secondOrderItem);

        when(discountPolicy.calculateDiscount(orderItems, new BigDecimal("110.00")))
                .thenReturn(new BigDecimal("11.00"));

        PricingService pricingService = new PricingService(discountPolicy);

        //Act
        BigDecimal finalPrice = pricingService.calculateFinalPrice(orderItems);

        //Assert
        assertThat(finalPrice).isEqualByComparingTo("99.00");

        verify(discountPolicy).calculateDiscount(orderItems, new BigDecimal("110.00"));
    }

    @Test
    void shouldReturnRegularPriceWhenDiscountIsZero() {
        //Arrange
        OrderItem orderItem = mock(OrderItem.class);

        when(orderItem.totalPrice()).thenReturn(new BigDecimal("100.00"));

        List<OrderItem> items = List.of(orderItem);

        when(discountPolicy.calculateDiscount(items, new BigDecimal("100.00"))).thenReturn(BigDecimal.ZERO);

        PricingService pricingService = new PricingService(discountPolicy);

        //Act
        BigDecimal finalPrice = pricingService.calculateFinalPrice(items);

        //Assert
        assertThat(finalPrice).isEqualByComparingTo("100.00");

        verify(discountPolicy).calculateDiscount(items, new BigDecimal("100.00"));
    }
}
