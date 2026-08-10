package onlineshop.cli;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ConsolePrinter {
    private final ZoneId displayZone;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ConsolePrinter(@NonNull ZoneId displayZone) {
        this.displayZone = displayZone;
    }

    public void print(String text) {
        log.info("\n{}", text);
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

        print(stringBuilder.toString());
    }

    public void printProduct(@NonNull Product product) {
        log.info("{} | {} | price: {} | available: {}",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity());
    }

    public void printCartItem(@NonNull CartItem cartItem) {
        log.info("{} | quantity: {} | unit price: {}",
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice());
    }

    public void printOrder(@NonNull Order order) {
        log.info("Order ID: {}\nTotal price: {}\nDate: {}",
                order.getOrderId(), order.getTotalPrice(), formatDate(order.getOrderDate()));
    }

    public void printInvoice(@NonNull Invoice invoice) {
        log.info("Invoice ID: {}\nInvoice number: {}\nDate: {}",
                invoice.getInvoiceId(), invoice.getInvoiceNumber(), formatDate(invoice.getIssueDate()));
    }

    public void printAccount(@NonNull Account account) {
        print(account.toString());
    }

    private String formatDate(Instant instant) {
        return FORMATTER.format(instant.atZone(displayZone));
    }
}
