package onlineshop;

import onlineshop.cli.ConsolePrinter;
import onlineshop.cli.DataReader;
import onlineshop.cli.ShopCli;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Electronics;
import onlineshop.domain.product.Smartphone;
import onlineshop.domain.useraccount.Account;
import onlineshop.repo.*;
import onlineshop.service.invoice.InvoiceGenerator;
import onlineshop.service.invoice.InvoiceNumberGenerator;
import onlineshop.service.invoice.InvoiceService;
import onlineshop.service.invoice.SequentialInvoiceNumberGenerator;
import onlineshop.service.order.OrderProcessor;
import onlineshop.service.product.ProductManager;

import java.math.BigDecimal;

public class ShopApp {
    public static void main(String[] args) {
        InvoiceNumberGenerator invoiceNumberGenerator = new SequentialInvoiceNumberGenerator();
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();
        InvoiceRepository invoiceRepository = new InMemoryInvoiceRepository();
        InvoiceGenerator invoiceService = new InvoiceService(invoiceNumberGenerator);
        ProductManager productManager = new ProductManager(productRepository);
        OrderProcessor orderProcessor = new OrderProcessor(
                productManager,
                orderRepository,
                invoiceRepository,
                invoiceService
        );
        Account account = createTestAccount();
        Cart cart = new Cart(account.getAccountId());
        ConsolePrinter consolePrinter = new ConsolePrinter();
        DataReader dataReader = new DataReader(consolePrinter);

        addTestProducts(productManager);

        new ShopCli(productManager, orderProcessor, account, cart, dataReader, consolePrinter).run();
    }

    private static Account createTestAccount() {
        return Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
    }

    private static void addTestProducts(ProductManager productManager) {
        productManager.addProduct(
                Computer.builder()
                        .id("C-001")
                        .name("testComputer")
                        .price(new BigDecimal("10"))
                        .quantity(2)
                        .cpu("Intel")
                        .ram("64 GB")
                        .build()
        );

        productManager.addProduct(
                Electronics.builder()
                        .id("E-001")
                        .name("testElectronics")
                        .price(new BigDecimal("10"))
                        .quantity(4)
                        .build()
        );

        productManager.addProduct(
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
