package onlineshop.cli;

import lombok.Getter;

@Getter
public enum Option implements CliOption {
    SHOW_PRODUCTS(1, "Show products"),
    ADD_PRODUCT_TO_CART(2, "Add product to cart"),
    SHOW_CART(3, "Show cart"),
    CHECKOUT(4, "Checkout"),
    ACCOUNT_INFO(5, "Account info"),
    SHOW_ORDERS(6, "Show orders"),
    SHOW_INVOICES(7, "Show invoices"),
    EXIT(0, "Exit");

    private final int optionNumber;
    private final String description;

    Option(int optionNumber, String description) {
        this.optionNumber = optionNumber;
        this.description = description;
    }

    public static Option fromNumber(int number) {
        Option option = null;

        for (Option optionValue : Option.values()) {
            if (optionValue.optionNumber == number) {
                option = optionValue;
            }
        }
        return option;
    }
}
