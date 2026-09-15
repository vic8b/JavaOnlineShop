package onlineshop.cli;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.service.invoice.InvoiceQueryService;
import onlineshop.service.order.OrderProcessingService;
import onlineshop.service.order.OrderQueryService;
import onlineshop.service.product.ProductInventoryService;

import java.util.List;

public class ShopCli {
    private final ProductInventoryService productInventoryService;
    private final OrderProcessingService orderProcessingService;
    private final Account account;
    private final Cart cart;
    private final OrderQueryService orderQueryService;
    private final InvoiceQueryService invoiceQueryService;
    private final DataReader dataReader;
    private final ConsolePrinter printer;

    public ShopCli(@NonNull ProductInventoryService productInventoryService,
                   @NonNull OrderProcessingService orderProcessingService,
                   @NonNull Account account,
                   @NonNull Cart cart,
                   @NonNull OrderQueryService orderQueryService,
                   @NonNull InvoiceQueryService invoiceQueryService,
                   @NonNull DataReader dataReader,
                   @NonNull ConsolePrinter printer
    ) {
        this.productInventoryService = productInventoryService;
        this.orderProcessingService = orderProcessingService;
        this.account = account;
        this.cart = cart;
        this.orderQueryService = orderQueryService;
        this.invoiceQueryService = invoiceQueryService;
        this.dataReader = dataReader;
        this.printer = printer;
    }

    public void run() {
        printer.printWelcomeMessage();

        Option option;

        do {
            printer.printMenu(Option.class);
            option = Option.fromNumber(dataReader.getOptionInt());

            handleOption(option);
        } while (option != Option.EXIT);
    }

    private void handleOption(Option option) {
        if (option == null) {
            printer.print("Invalid option");
            return;
        }

        switch (option) {
            case SHOW_PRODUCTS -> showProducts();
            case ADD_PRODUCT_TO_CART -> addProductToCart();
            case SHOW_CART -> showCart();
            case CHECKOUT -> checkout();
            case ACCOUNT_INFO -> showAccountInfo();
            case SHOW_ORDERS -> showOrders();
            case SHOW_INVOICES -> showInvoices();
            case EXIT -> printer.print("End of program");
        }
    }

    private void showProducts() {
        printer.print("Products:");
        List<Product> allProducts = productInventoryService.findAllProducts();

        if (allProducts.isEmpty()) {
            printer.print("No products available");
            return;
        }

        allProducts.forEach(printer::printProduct);
    }

    private void addProductToCart() {
        String productId = dataReader.readLine("Enter product id: ");
        int quantity = dataReader.readInt("Enter quantity: ");

        try {
            Product product = productInventoryService.findProductById(productId);
            cart.addProduct(product, quantity);

            printer.print("Product has been added to the cart");
        } catch (ProductNotFoundException | IllegalArgumentException e) {
            printer.print(e.getMessage());
        }
    }

    private void showCart() {
        printer.print("Cart:");

        if (cart.isEmpty()) {
            printer.print("Your cart is empty");
            return;
        }

        cart.getItems().forEach(printer::printCartItem);
    }

    private void checkout() {
        try {
            Order order = orderProcessingService.processCheckout(account, cart);

            printer.print("Order placed successfully");
            printer.printOrder(order);
        } catch (IllegalArgumentException | ProductUnavailableException e) {
            printer.print(e.getMessage());
        }
    }

    private void showAccountInfo() {
        printer.print("Account info:");
        printer.printAccount(account);
    }

    private void showOrders() {
        printer.print("Orders:");

        List<Order> orders = orderQueryService.findOrdersForAccount(account.getAccountId());

        if (orders.isEmpty()) {
            printer.print("No orders found");
            return;
        }

        orders.forEach(printer::printOrder);
    }

    private void showInvoices() {
        printer.print("Invoices:");

        List<Invoice> invoices = invoiceQueryService.findInvoicesForAccount(account.getAccountId());

        if (invoices.isEmpty()) {
            printer.print("No invoices found");
            return;
        }

        invoices.forEach(printer::printInvoice);
    }
}
