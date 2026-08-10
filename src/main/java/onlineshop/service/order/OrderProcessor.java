package onlineshop.service.order;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.domain.invoice.Invoice;
import onlineshop.repo.InvoiceRepository;
import onlineshop.repo.OrderRepository;
import onlineshop.service.discount.PricingService;
import onlineshop.service.product.ProductInventoryService;
import onlineshop.service.invoice.InvoiceGenerator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class OrderProcessor {
    private final ProductInventoryService productInventoryService;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGenerator invoiceGenerator;
    private final PricingService pricingService;
    private final Clock clock;

    private final Object productStockLock = new Object();

    public OrderProcessor(
            @NonNull ProductInventoryService productInventoryService,
            @NonNull OrderRepository orderRepository,
            @NonNull InvoiceRepository invoiceRepository,
            @NonNull InvoiceGenerator invoiceGenerator,
            @NonNull PricingService pricingService,
            @NonNull Clock clock
    ) {
        this.productInventoryService = productInventoryService;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceGenerator = invoiceGenerator;
        this.pricingService = pricingService;
        this.clock = clock;
    }

    public Order processCheckout(@NonNull Account account, @NonNull Cart cart) {
        validateCartState(account, cart);

        List<OrderItem> orderItems;

        synchronized (productStockLock) {
            validateAvailability(cart);

            orderItems = createOrderItems(cart);

            decreaseProductStock(orderItems);
        }

        BigDecimal finalPrice = pricingService.calculateFinalPrice(orderItems);

        Order order = createOrder(account, orderItems, finalPrice);
        orderRepository.add(order);

        Invoice invoice = invoiceGenerator.generate(order);
        invoiceRepository.add(invoice);

        cart.clear();

        return order;
    }

    private static void validateCartState(Account account, Cart cart) {
        if (cart.isEmpty()) throw new IllegalArgumentException("Cart cannot be empty");
        if (!cart.getAccountId().equals(account.getAccountId())) {
            throw new IllegalArgumentException("Cart doesn't belong to the provided account");
        }
    }

    private void validateAvailability(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = productInventoryService.findProductById(item.getProduct().getId());

            if (product.getQuantity() < item.getQuantity())
                throw new ProductUnavailableException(product.getId(), item.getQuantity(), product.getQuantity());
        }
    }

    private List<OrderItem> createOrderItems(@NonNull Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
                    Product product = productInventoryService.findProductById(cartItem.getProduct().getId());

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .toList();
    }

    private void decreaseProductStock(List<OrderItem> orderItems) {
        orderItems.forEach(item -> productInventoryService.decreaseStock(item.product().getId(), item.quantity()));
    }

    private Order createOrder(Account account, List<OrderItem> orderItems, BigDecimal finalPrice) {
        return Order.builder()
                .account(account)
                .items(orderItems)
                .totalPrice(finalPrice)
                .orderDate(Instant.now(clock))
                .build();
    }
}
