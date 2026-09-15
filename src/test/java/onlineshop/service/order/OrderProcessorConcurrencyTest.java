package onlineshop.service.order;

import onlineshop.discount.NoDiscountPolicy;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.repo.InMemoryInvoiceRepository;
import onlineshop.repo.InMemoryOrderRepository;
import onlineshop.repo.InMemoryProductRepository;
import onlineshop.repo.InvoiceRepository;
import onlineshop.repo.OrderRepository;
import onlineshop.repo.ProductRepository;
import onlineshop.service.discount.PricingService;
import onlineshop.service.invoice.InvoiceGenerator;
import onlineshop.service.product.ProductManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProcessorConcurrencyTest {
    private static final Instant TEST_INSTANT =
            Instant.parse("2026-08-05T12:00:00Z");

    private ExecutorService executorService;
    private Product product;
    private OrderProcessor orderProcessor;
    private OrderRepository orderRepository;
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    void setup() {
        executorService = Executors.newFixedThreadPool(2);

        ProductRepository productRepository = new InMemoryProductRepository();

        orderRepository = new InMemoryOrderRepository();
        invoiceRepository = new InMemoryInvoiceRepository();

        ProductManager productManager = new ProductManager(productRepository);

        PricingService pricingService = new PricingService(new NoDiscountPolicy());

        Clock clock = Clock.fixed(
                TEST_INSTANT,
                ZoneOffset.UTC
        );

        AtomicInteger invoiceCounter = new AtomicInteger(1);

        InvoiceGenerator invoiceGenerator = order -> new Invoice(
                UUID.randomUUID(),
                "INV-2026/" + invoiceCounter.getAndIncrement(),
                order,
                TEST_INSTANT
        );

        orderProcessor = new OrderProcessor(
                productManager,
                orderRepository,
                invoiceRepository,
                invoiceGenerator,
                pricingService,
                clock
        );

        product = Electronics.builder()
                .id("P-001")
                .name("Last product")
                .price(new BigDecimal("100.00"))
                .quantity(1)
                .build();

        productManager.addProduct(product);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void shouldAllowOnlyOneOrderWhenTwoThreadsBuyLastProduct() throws Exception {
        //Arrange
        Account firstAccount = createAccount("ACC-1");
        Account secondAccount = createAccount("ACC-2");

        Cart firstCart = createCart(firstAccount);
        Cart secondCart = createCart(secondAccount);

        //Act
        Future<Boolean> firstResult = executorService.submit(
                () -> processOrder(firstAccount, firstCart)
        );

        Future<Boolean> secondResult = executorService.submit(
                () -> processOrder(secondAccount, secondCart)
        );

        long successfulOrders = Stream.of(firstResult.get(), secondResult.get())
                .filter(Boolean::booleanValue)
                .count();

        //Assert
        assertThat(successfulOrders).isEqualTo(1);
        assertThat(product.getQuantity()).isZero();
        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(invoiceRepository.findAll()).hasSize(1);
    }

    private boolean processOrder(Account account, Cart cart) {
        try {
            orderProcessor.process(account, cart);
            return true;
        } catch (ProductUnavailableException e) {
            return false;
        }
    }

    private Account createAccount(String accountId) {
        return Account.builder()
                .accountId(accountId)
                .firstName("John")
                .lastName("Doe")
                .email(accountId.toLowerCase() + "@example.com")
                .build();
    }

    private Cart createCart(Account account) {
        Cart cart = new Cart(account.getAccountId());
        cart.addProduct(product, 1);
        return cart;
    }
}