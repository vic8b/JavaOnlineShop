package onlineshop.cli;

import lombok.NonNull;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ConsolePrinter {
    private final ZoneId displayZone;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ConsolePrinter(@NonNull ZoneId displayZone) {
        this.displayZone = displayZone;
    }

    public void print(String text) {
        System.out.println(text);
    }

    public void printWelcomeMessage() {
        print("Welcome to the online shop!");
    }

    public <E extends Enum<E> & CliOption> void printMenu(Class<E> enumType) {
        StringBuilder stringBuilder = new StringBuilder();

        for (E option : enumType.getEnumConstants()) {
            stringBuilder.append(option.getOptionNumber())
                    .append(" - ")
                    .append(option.name())
                    .append(" - ")
                    .append(option.getDescription())
                    .append(System.lineSeparator());


        }

        print(String.valueOf(stringBuilder));
    }

    public void printProduct(@NonNull Product product) {
        System.out.printf("%s | %s | price: %s | available: %d%n",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity());
    }

    public void printCartItem(@NonNull CartItem cartItem) {
        System.out.printf("%s | quantity: %d | unit price: %s%n",
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice());
    }

    private String formatDate(Instant instant) {
        return FORMATTER.format(instant.atZone(displayZone));
    }

    public void printOrder(@NonNull Order order) {
        System.out.printf("Order ID: %s%nTotal price: %s%nDate: %s%n",
                order.getOrderId(), order.getTotalPrice(), formatDate(order.getOrderDate()));
    }

    public void printInvoice(@NonNull Invoice invoice) {
        System.out.printf("Invoice ID: %s%nInvoice number: %s%nDate: %s%n",
                invoice.getInvoiceId(), invoice.getInvoiceNumber(), formatDate(invoice.getIssueDate()));
    }

    public void printAccount(@NonNull Account account) {
        print(account.toString());
    }
}
