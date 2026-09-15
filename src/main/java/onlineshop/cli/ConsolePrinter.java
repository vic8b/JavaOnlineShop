package onlineshop.cli;

import lombok.NonNull;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;

public class ConsolePrinter {
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

    public void printOrder(@NonNull Order order) {
        System.out.printf("Order ID: %s%nTotal price: %s%nDate: %s%n",
                order.getOrderId(), order.getTotalPrice(), order.getOrderDate());
    }

    public void printAccount(@NonNull Account account) {
        print(account.toString());
    }
}
