package onlineshop.cli;

import onlineshop.domain.cart.Cart;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.service.invoice.InvoiceQueryService;
import onlineshop.service.order.OrderProcessingService;
import onlineshop.service.order.OrderQueryService;
import onlineshop.service.product.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopCliTest {

    @Mock
    private ProductManager productManager;

    @Mock
    private OrderProcessingService orderProcessingService;

    @Mock
    private Account account;

    @Mock
    private Cart cart;

    @Mock
    private OrderQueryService orderQueryService;

    @Mock
    private InvoiceQueryService invoiceQueryService;

    @Mock
    private DataReader dataReader;

    @Mock
    private ConsolePrinter consolePrinter;

    private ShopCli shopCli;

    @BeforeEach
    void setup() {
        shopCli = new ShopCli(
                productManager,
                orderProcessingService,
                account,
                cart,
                orderQueryService,
                invoiceQueryService,
                dataReader,
                consolePrinter
        );
    }

    //SHOW_PRODUCTS
    @Test
    void shouldShowAllProductsAndExit() {
        //Arrange
        Product product = mock(Product.class);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_PRODUCTS.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(productManager.findAllProducts()).thenReturn(List.of(product));

        //Act
        shopCli.run();

        //Assert
        verify(productManager).findAllProducts();
        verify(consolePrinter).printProduct(product);
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldInformThatProductIsNotAvailable() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_PRODUCTS.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(productManager.findAllProducts()).thenReturn(List.of());

        //Act
        shopCli.run();

        //Assert
        verify(productManager).findAllProducts();
        verify(consolePrinter).print("No products available");
    }

    // ADD_PRODUCT_TO_CART
    @Test
    void shouldFindProductAndAddProductToCart() {
        //Arrange + Act
        Product product = mock(Product.class);

        when (dataReader.getOptionInt())
                .thenReturn(
                        Option.ADD_PRODUCT_TO_CART.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(dataReader.readLine("Enter product id: ")).thenReturn("1");
        when(dataReader.readInt("Enter quantity: ")).thenReturn(1);

        when(productManager.findProductById("1")).thenReturn(product);

        shopCli.run();

        //Assert
        verify(productManager).findProductById("1");
        verify(consolePrinter).print("Product has been added to the cart");
        verify(cart).addProduct(product, 1);
    }

    @Test
    void shouldHandleNonExistingProductWhenAddedToCart() {
        //Arrange
        when (dataReader.getOptionInt())
                .thenReturn(
                        Option.ADD_PRODUCT_TO_CART.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(dataReader.readLine("Enter product id: ")).thenReturn("1");
        when(dataReader.readInt("Enter quantity: ")).thenReturn(1);

        when(productManager.findProductById("1")).thenThrow(new ProductNotFoundException("1"));

        //Act
        shopCli.run();

        //Act + Assert
        verify(productManager).findProductById("1");
        verify(consolePrinter).print("Product with id 1 not found");
        verify(cart, never()).addProduct(any(), anyInt());
    }

    @Test
    void shouldHandleInsufficientQuantityWhenAddedToCart() {
        //Arrange
        Product product = mock(Product.class);

        when (dataReader.getOptionInt())
                .thenReturn(
                        Option.ADD_PRODUCT_TO_CART.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(dataReader.readLine("Enter product id: ")).thenReturn("1");
        when(dataReader.readInt("Enter quantity: ")).thenReturn(1);
        when(productManager.findProductById("1")).thenReturn(product);

        doThrow(new IllegalArgumentException("Required quantity exceeds item availability"))
                .when(cart).addProduct(product, 1);

        //Act
        shopCli.run();

        //Act + Assert
        verify(productManager).findProductById("1");
        verify(cart).addProduct(product, 1);
        verify(consolePrinter).print("Required quantity exceeds item availability");
    }

    // SHOW_CART
    @Test
    void shouldShowCartAndExit() {
        //Arrange
        CartItem cartItem = mock(CartItem.class);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_CART.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(cart.getItems()).thenReturn(List.of(cartItem));

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Cart:");
        verify(consolePrinter).printCartItem(cartItem);
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldInformThatTheCartIsEmpty() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_CART.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(cart.isEmpty()).thenReturn(true);

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Cart:");
        verify(consolePrinter).print("Your cart is empty");
        verify(consolePrinter).print("End of program");
    }

    // CHECKOUT
    @Test
    void shouldProcessOrderAndPrintConfirmation() {
        //Arrange
        Order order = mock(Order.class);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.CHECKOUT.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(orderProcessingService.process(account, cart)).thenReturn(order);

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Order placed successfully");
        verify(consolePrinter).printOrder(order);
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldHandleProcessingEmptyCart() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.CHECKOUT.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(orderProcessingService.process(account, cart)).thenThrow(new IllegalArgumentException("Cart cannot be empty"));

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Cart cannot be empty");
        verify(consolePrinter).print("End of program");
        verify(consolePrinter, never()).print("Order placed successfully");
    }

    @Test
    void shouldHandleProcessingWithUnavailableProduct() {
        //Arrange
        ProductUnavailableException productUnavailableException = new ProductUnavailableException("1", 3, 2);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.CHECKOUT.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(orderProcessingService.process(account, cart)).thenThrow(productUnavailableException);

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print(productUnavailableException.getMessage());
        verify(consolePrinter).print("End of program");
        verify(consolePrinter, never()).printOrder(any());
        verify(consolePrinter, never()).print("Order placed successfully");
    }

    @Test
    void shouldExitLoop() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.EXIT.getOptionNumber()
                );

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldInformAboutInvalidOptionAndContinueRunning() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        999,
                        Option.EXIT.getOptionNumber()
                );

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Invalid option");
        verify(consolePrinter).print("End of program");
    }

    //ACCOUNT_INFO
    @Test
    void shouldShowAccountInfo() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.ACCOUNT_INFO.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Account info:");
        verify(consolePrinter).printAccount(account);
        verify(consolePrinter).print("End of program");
    }

    //SHOW_ORDERS
    @Test
    void shouldShowOrdersAndExit() {
        //Arrange
        Order order = mock(Order.class);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_ORDERS.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(orderQueryService.findOrdersForAccount(account.getAccountId()))
                .thenReturn(List.of(order));

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Orders:");
        verify(consolePrinter).printOrder(order);
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldInformThatOrdersAreEmpty() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_ORDERS.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(orderQueryService.findOrdersForAccount(account.getAccountId()))
                .thenReturn(List.of());

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Orders:");
        verify(consolePrinter).print("No orders found");
        verify(consolePrinter).print("End of program");
    }

    //SHOW_INVOICES
    @Test
    void shouldShowInvoicesAndExit() {
        //Arrange
        Invoice invoice = mock(Invoice.class);

        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_INVOICES.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(invoiceQueryService.findInvoicesForAccount(account.getAccountId()))
                .thenReturn(List.of(invoice));

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Invoices:");
        verify(consolePrinter).printInvoice(invoice);
        verify(consolePrinter).print("End of program");
    }

    @Test
    void shouldInformThatInvoicesAreEmpty() {
        //Arrange
        when(dataReader.getOptionInt())
                .thenReturn(
                        Option.SHOW_INVOICES.getOptionNumber(),
                        Option.EXIT.getOptionNumber()
                );

        when(invoiceQueryService.findInvoicesForAccount(account.getAccountId()))
                .thenReturn(List.of());

        //Act
        shopCli.run();

        //Assert
        verify(consolePrinter).print("Invoices:");
        verify(consolePrinter).print("No invoices found");
        verify(consolePrinter).print("End of program");
    }
}
