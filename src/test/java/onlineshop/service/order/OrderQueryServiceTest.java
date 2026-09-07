package onlineshop.service.order;

import onlineshop.domain.order.Order;
import onlineshop.domain.useraccount.Account;
import onlineshop.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Order firstOrder;

    @Mock
    private Order secondOrder;

    @Mock
    private Account firstAccount;

    @Mock
    private Account secondAccount;

    @Test
    void shouldFindOrdersForAccount() {
        //Arrange
        when(firstOrder.getAccount()).thenReturn(firstAccount);
        when(secondOrder.getAccount()).thenReturn(secondAccount);

        when(firstAccount.getAccountId()).thenReturn("ACC-1");
        when(secondAccount.getAccountId()).thenReturn("ACC-2");

        when(orderRepository.findAll()).thenReturn(List.of(firstOrder, secondOrder));

        OrderQueryService orderQueryService = new OrderQueryService(orderRepository);

        //Act
        List<Order> result = orderQueryService.findOrdersForAccount("ACC-1");

        //Assert
        assertThat(result).containsExactly(firstOrder);
    }

    @Test
    void shouldReturnEmptyListWhenAccountHasNoOrders() {
        //Arrange
        when(orderRepository.findAll()).thenReturn(List.of());
        OrderQueryService service = new OrderQueryService(orderRepository);

        //Act
        List<Order> result = service.findOrdersForAccount("ACC-1");

        //Assert
        assertThat(result).isEmpty();
    }
}
