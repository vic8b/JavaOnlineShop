package onlineshop.service.order;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.order.Order;
import onlineshop.domain.useraccount.Account;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

// Design pattern: Facade
// Provides a simplified synchronous/asynchronous entry point to the checkout workflow.

public class OrderProcessingService implements AutoCloseable {
    private final OrderProcessor orderProcessor;
    private final ExecutorService executorService;

    public OrderProcessingService(
            @NonNull OrderProcessor orderProcessor,
            @NonNull ExecutorService executorService
    ) {
        this.orderProcessor = orderProcessor;
        this.executorService = executorService;
    }

    public Order processCheckout(@NonNull Account account, @NonNull Cart cart) {
        return orderProcessor.processCheckout(account, cart);
    }

    public CompletableFuture<Order> processCheckoutAsync(@NonNull Account account, @NonNull Cart cart) {
        return CompletableFuture.supplyAsync(
                () -> orderProcessor.processCheckout(account, cart),
                executorService
        );
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
