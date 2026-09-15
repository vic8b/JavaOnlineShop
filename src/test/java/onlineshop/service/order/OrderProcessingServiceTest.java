package onlineshop.service.order;

import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.order.Order;
import onlineshop.domain.useraccount.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {
    private static final int ORDER_COUNT = 100;
    private static final long PROCESSING_DELAY_IN_MS = 50;

    @Mock
    OrderProcessor orderProcessor;

    @Mock
    Account account;

    @Mock
    Cart cart;

    @Mock
    Order order;

    ExecutorService executorService;
    OrderProcessingService orderProcessingService;

    @BeforeEach
    void setup() {
        executorService = Executors.newFixedThreadPool(3);

        orderProcessingService = new OrderProcessingService(orderProcessor, executorService);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void shouldDelegateSynchronousProcessing() {
        //Arrange
        when(orderProcessor.processCheckout(account, cart))
                .thenReturn(order);

        //Act
        Order result = orderProcessingService.processCheckout(account, cart);

        //Assert
        assertThat(result).isSameAs(order);
        verify(orderProcessor).processCheckout(account, cart);
    }

    @Test
    void shouldProcessCheckoutAsynchronously() {
        //Arrange
        when(orderProcessor.processCheckout(account, cart))
                .thenReturn(order);

        //Act
        CompletableFuture<Order> result = orderProcessingService.processCheckoutAsync(account, cart);

        //Assert
        assertThat(result.join()).isSameAs(order);
        verify(orderProcessor).processCheckout(account, cart);
    }

    @Test
    void shouldCompleteFutureExceptionallyWhenProcessingFails() {
        //Arrange
        when(orderProcessor.processCheckout(account, cart))
                .thenThrow(new IllegalArgumentException("Invalid cart"));

        //Act
        CompletableFuture<Order> future = orderProcessingService.processCheckoutAsync(account, cart);

        //Assert
        assertThat(future).isCompletedExceptionally();

        assertThatThrownBy(future::join)
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("Invalid cart");
    }

    @Test
    void shouldProcessOrdersFasterAsynchronouslyThanSynchronously() {
        List<Account> accounts = createAccounts();
        List<Cart> carts = createCarts();
        List<Order> orders = createOrders();

        mockDelayedProcessing(accounts, carts, orders);

        long synchronousStart = System.nanoTime();

        List<Order> synchronousOrders = processCheckoutSynchronously(accounts, carts);

        long synchronousDuration = System.nanoTime() - synchronousStart;

        long asynchronousStart = System.nanoTime();

        List<Order> asynchronousOrders = processCheckoutAsynchronously(accounts, carts);

        long asynchronousDuration = System.nanoTime() - asynchronousStart;

        assertThat(synchronousOrders).containsExactlyElementsOf(orders);

        assertThat(asynchronousOrders).containsExactlyElementsOf(orders);

        assertThat(asynchronousDuration).isLessThan(synchronousDuration);

        log.info("Synchronous processing: {} ms\n", TimeUnit.NANOSECONDS.toMillis(synchronousDuration));
        log.info("Asynchronous processing: {} ms\n", TimeUnit.NANOSECONDS.toMillis(asynchronousDuration));
    }

    private List<Order> processCheckoutAsynchronously(List<Account> accounts, List<Cart> carts) {
        List<CompletableFuture<Order>> futures = IntStream.range(0, accounts.size())
                .mapToObj(index -> orderProcessingService.processCheckoutAsync(accounts.get(index), carts.get(index)))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private List<Order> processCheckoutSynchronously(List<Account> accounts, List<Cart> carts) {
        return IntStream.range(0, accounts.size())
                .mapToObj(index -> orderProcessingService.processCheckout(accounts.get(index), carts.get(index)))
                .toList();
    }

    private void mockDelayedProcessing(List<Account> accounts, List<Cart> carts, List<Order> orders) {
        for (int i = 0; i < orders.size(); i++) {
            Account currentAccount = accounts.get(i);
            Cart currentCart = carts.get(i);
            Order expectedOrder = orders.get(i);

            when(orderProcessor.processCheckout(currentAccount, currentCart))
                    .thenAnswer(invocationOnMock -> {
                        Thread.sleep(PROCESSING_DELAY_IN_MS);
                        return expectedOrder;
                    });
        }
    }

    private List<Account> createAccounts() {
        return IntStream.range(0, ORDER_COUNT)
                .mapToObj(index -> mock(Account.class))
                .toList();
    }

    private List<Cart> createCarts() {
        return IntStream.range(0, ORDER_COUNT)
                .mapToObj(index -> mock(Cart.class))
                .toList();
    }

    private List<Order> createOrders() {
        return IntStream.range(0, ORDER_COUNT)
                .mapToObj(index -> mock(Order.class))
                .toList();
    }
}
