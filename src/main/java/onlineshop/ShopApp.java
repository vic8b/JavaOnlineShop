package onlineshop;

import onlineshop.cli.ConsolePrinter;
import onlineshop.cli.DataReader;
import onlineshop.cli.ShopCli;
import onlineshop.discount.DiscountPolicy;
import onlineshop.discount.OrderValuePercentageDiscount;
import onlineshop.discount.ProductPercentageDiscount;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Smartphone;
import onlineshop.domain.useraccount.Account;
import onlineshop.domain.useraccount.Email;
import onlineshop.repo.*;
import onlineshop.service.discount.PricingService;
import onlineshop.service.invoice.*;
import onlineshop.service.order.OrderProcessingService;
import onlineshop.service.order.OrderProcessor;
import onlineshop.service.order.OrderQueryService;
import onlineshop.service.product.ProductInventoryService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.Executors;

public class ShopApp {
    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new FileOrderRepository(Path.of("data/orders/orders.txt"));
        InvoiceRepository invoiceRepository = new FileInvoiceRepository(
                Path.of("data/invoices/invoices.txt"),
                orderRepository);
        InvoiceNumberGenerator invoiceNumberGenerator =
                new SequentialInvoiceNumberGenerator(invoiceRepository.findAll(), clock);

        InvoiceGenerator invoiceService = new InvoiceService(invoiceNumberGenerator, clock);
        ProductInventoryService productInventoryService = new ProductInventoryService(productRepository);
        DiscountPolicy discountPolicy =
                new OrderValuePercentageDiscount(
                        new BigDecimal("500"),
                        new BigDecimal("10")
                )
                .andThen(new ProductPercentageDiscount("C-001", new BigDecimal("5")));
        PricingService pricingService = new PricingService(discountPolicy);
        OrderProcessor orderProcessor = new OrderProcessor(
                productInventoryService,
                orderRepository,
                invoiceRepository,
                invoiceService,
                pricingService,
                clock
        );
        Account account = createTestAccount();
        Cart cart = new Cart(account.getAccountId());
        OrderQueryService orderQueryService = new OrderQueryService(orderRepository);
        InvoiceQueryService invoiceQueryService = new InvoiceQueryService(invoiceRepository);
        ConsolePrinter consolePrinter = new ConsolePrinter(zoneId);
        DataReader dataReader = new DataReader(consolePrinter);

        addTestProducts(productInventoryService);

        try (OrderProcessingService orderProcessingService
                     = new OrderProcessingService(
                orderProcessor, Executors.newFixedThreadPool(4))
        ) {

            new ShopCli(
                    productInventoryService,
                    orderProcessingService,
                    account,
                    cart,
                    orderQueryService,
                    invoiceQueryService,
                    dataReader,
                    consolePrinter
            ).run();
        }
    }

    private static Account createTestAccount() {
        return Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@example.com"))
                .build();
    }

    private static void addTestProducts(ProductInventoryService productInventoryService) {
        productInventoryService.addProduct(
                Computer.builder()
                        .id("C-001")
                        .name("testComputer")
                        .price(new BigDecimal("500"))
                        .quantity(2)
                        .cpu("Intel")
                        .ram("64 GB")
                        .build()
        );

        productInventoryService.addProduct(
                Electronics.builder()
                        .id("E-001")
                        .name("testElectronics")
                        .price(new BigDecimal("10"))
                        .quantity(4)
                        .build()
        );

        productInventoryService.addProduct(
                Smartphone.builder()
                        .id("S-001")
                        .name("testSmartphone")
                        .price(new BigDecimal("10"))
                        .quantity(10)
                        .color("yellow")
                        .batteryCapacity("1050 mAh")
                        .build()
        );
    }
}
