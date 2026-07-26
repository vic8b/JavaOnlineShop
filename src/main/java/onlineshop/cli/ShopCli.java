package onlineshop.cli;

import lombok.NonNull;
import onlineshop.domain.cart.Cart;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.service.order.OrderProcessor;
import onlineshop.service.product.ProductManager;

import java.util.List;

public class ShopCli {
    private final ProductManager productManager;
    private final OrderProcessor orderProcessor;
    private final Account account;
    private final Cart cart;
    private final DataReader dataReader;
    private final ConsolePrinter printer;

    public ShopCli(@NonNull ProductManager productManager,
                   @NonNull OrderProcessor orderProcessor,
                   @NonNull Account account,
                   @NonNull Cart cart,
                   @NonNull DataReader dataReader,
                   @NonNull ConsolePrinter printer
    ) {
        this.productManager = productManager;
        this.orderProcessor = orderProcessor;
        this.account = account;
        this.cart = cart;
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
            case EXIT -> printer.print("End of program");
        }
    }

    private void showProducts() {
        printer.print("Products:");
        List<Product> allProducts = productManager.findAllProducts();

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
            Product product = productManager.findProductById(productId);
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
            Order order = orderProcessor.process(account, cart);

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
}
