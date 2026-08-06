package onlineshop.service.order;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.order.Order;
import onlineshop.domain.useraccount.Account;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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

    public Order process(@NonNull Account account, @NonNull Cart cart) {
        return orderProcessor.process(account, cart);
    }

    public CompletableFuture<Order> processAsync(@NonNull Account account, @NonNull Cart cart) {
        return CompletableFuture.supplyAsync(
                () -> orderProcessor.process(account, cart),
                executorService
        );
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
