package onlineshop.service.order;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.order.OrderItem;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.repo.InvoiceRepository;
import onlineshop.repo.OrderRepository;
import onlineshop.service.discount.PricingService;
import onlineshop.service.invoice.InvoiceGenerator;
import onlineshop.service.product.ProductInventoryService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public class OrderProcessor {
    private final ProductInventoryService productInventoryService;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGenerator invoiceGenerator;
    private final PricingService pricingService;
    private final Clock clock;

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

        List<CartItem> cartItems = cart.getItems();

        List<Product> reservedProducts = productInventoryService.reserveStock(cartItems);

        List<OrderItem> orderItems = createOrderItems(cartItems, reservedProducts);

        BigDecimal finalPrice = pricingService.calculateFinalPrice(orderItems);

        Order order = createOrder(account, orderItems, finalPrice);
        orderRepository.add(order);

        Invoice invoice = invoiceGenerator.generate(order);
        invoiceRepository.add(invoice);

        cart.clear();

        return order;
    }

    private static void validateCartState(@NonNull Account account, @NonNull Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }
        if (!cart.getAccountId().equals(account.getAccountId())) {
            throw new IllegalArgumentException("Cart doesn't belong to the provided account");
        }
    }

    private List<OrderItem> createOrderItems(@NonNull List<CartItem> cartItems, @NonNull List<Product> reservedProducts) {
        return IntStream.range(0, cartItems.size())
                .mapToObj(index -> {
                    CartItem cartItem = cartItems.get(index);
                    Product product = reservedProducts.get(index);

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .toList();
    }

    private Order createOrder(@NonNull Account account, @NonNull List<OrderItem> orderItems, @NonNull BigDecimal finalPrice) {
        return Order.builder()
                .account(account)
                .items(orderItems)
                .totalPrice(finalPrice)
                .orderDate(Instant.now(clock))
                .build();
    }
}
