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
import onlineshop.service.product.ProductManager;
import onlineshop.service.invoice.InvoiceGenerator;

import java.util.List;

public class OrderProcessor {
    private final ProductManager productManager;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGenerator invoiceGenerator;

    public OrderProcessor(
            @NonNull ProductManager productManager,
            @NonNull OrderRepository orderRepository,
            @NonNull InvoiceRepository invoiceRepository,
            @NonNull InvoiceGenerator invoiceGenerator
    ) {
        this.productManager = productManager;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceGenerator = invoiceGenerator;
    }

    public Order process(@NonNull Account account, @NonNull Cart cart) {
        if (cart.isEmpty()) throw new IllegalArgumentException("Cart cannot be empty");
        if (!cart.getAccountId().equals(account.getAccountId())) {
            throw new IllegalArgumentException("Cart doesn't belong to the provided account");
        }

        validateAvailability(cart);

        List<OrderItem> orderItems = createOrderItems(cart);

        decreaseProductStock(orderItems);

        Order order = createOrder(account, orderItems);
        orderRepository.add(order);

        Invoice invoice = invoiceGenerator.generate(order);
        invoiceRepository.add(invoice);

        cart.clear();

        return order;
    }

    private void validateAvailability(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = productManager.findProductById(item.getProduct().getId());

            if (product.getQuantity() < item.getQuantity())
                throw new ProductUnavailableException(product.getId(), item.getQuantity(), product.getQuantity());
        }
    }

    private List<OrderItem> createOrderItems(@NonNull Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
                    Product product = productManager.findProductById(cartItem.getProduct().getId());

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .toList();
    }

    private void decreaseProductStock(List<OrderItem> orderItems) {
        orderItems.forEach(item -> productManager.decreaseStock(item.getProduct().getId(), item.getQuantity()));
    }

    private Order createOrder(Account account, List<OrderItem> orderItems) {
        return Order.builder()
                .account(account)
                .items(orderItems)
                .build();
    }
}
