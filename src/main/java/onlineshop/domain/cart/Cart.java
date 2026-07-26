package onlineshop.domain.cart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import onlineshop.domain.product.Product;
import onlineshop.exception.CartItemNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
public class Cart {
    @NonNull
    private final String accountId;
    private final Map<String, CartItem> items = new HashMap<>();

    public Cart(@NonNull String accountId) {
        this.accountId = accountId;
    }

    public void addProduct(@NonNull Product product, int quantity) {
        CartItem cartItem = items.get(product.getId());

        if (cartItem == null) {
            items.put(product.getId(), new CartItem(product, quantity));
            System.out.println("Item has been added to the cart");
        } else {
            cartItem.changeQuantity(cartItem.getQuantity() + quantity);
            System.out.println("Item already in the cart. Quantity has been changed to " + cartItem.getQuantity());
        }
    }

    public boolean removeProduct(@NonNull String productId) {
        return items.remove(productId) != null;
    }

    public void changeQuantity(@NonNull String productId, int newQuantity) {
        CartItem cartItem = items.get(productId);

        if (cartItem == null) {
            throw new CartItemNotFoundException(productId);
        }

        cartItem.changeQuantity(newQuantity);
    }

    public List<CartItem> getItems() {
        return List.copyOf(items.values());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
