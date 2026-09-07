package onlineshop.service.order;

import onlineshop.domain.cart.Cart;
import onlineshop.domain.cart.CartItem;
import onlineshop.domain.invoice.Invoice;
import onlineshop.domain.order.Order;
import onlineshop.domain.product.Product;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.repo.InvoiceRepository;
import onlineshop.repo.OrderRepository;
import onlineshop.service.discount.PricingService;
import onlineshop.service.invoice.InvoiceGenerator;
import onlineshop.service.product.ProductInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    @Mock
    ProductInventoryService productInventoryService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    InvoiceGenerator invoiceGenerator;

    @Mock
    Account account;

    @Mock
    Cart cart;

    @Mock
    CartItem cartItem;

    @Mock
    Product product;

    @Mock
    Invoice invoice;

    @Mock
    PricingService pricingService;

    private static final Instant FIXED_INSTANT = Instant.parse("2026-08-02T12:00:00Z");

    private static final Clock CLOCK = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

    OrderProcessor orderProcessor;

    @BeforeEach
    void setup() {
        orderProcessor = new OrderProcessor(
                productInventoryService,
                orderRepository,
                invoiceRepository,
                invoiceGenerator,
                pricingService,
                CLOCK
        );
    }

    @Test
    void shouldSuccessfullyCreateOrder() {
        //Arrange
        prepareValidOrder();

        when(invoiceGenerator.generate(any(Order.class))).thenReturn(invoice);

        when(pricingService.calculateFinalPrice(anyList())).thenReturn(new BigDecimal("180.00"));

        when(productInventoryService.reserveStock(anyList())).thenReturn(List.of(product));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        //Act
        Order result = orderProcessor.processCheckout(account, cart);

        //Assert
        verify(productInventoryService).reserveStock(List.of(cartItem));

        verify(orderRepository).add(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertThat(result).isSameAs(savedOrder);
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getOrderId());
        assertThat(result.getAccount()).isSameAs(savedOrder.getAccount());
        assertThat(result.getItems()).isSameAs(savedOrder.getItems());
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().product()).isSameAs(product);
        assertThat(result.getItems().getFirst().quantity()).isEqualTo(2);
        assertThat(result.getItems().getFirst().unitPrice()).isEqualByComparingTo("100.00");
        assertThat(result.getTotalPrice()).isEqualByComparingTo("180.00");
        assertThat(result.getOrderDate()).isEqualTo(FIXED_INSTANT);

        verify(invoiceGenerator).generate(result);
        verify(invoiceRepository).add(invoice);
        verify(pricingService).calculateFinalPrice(anyList());
        verify(cart).clear();
    }

    @Test
    void shouldProcessCheckoutAllProductsFromTheCart() {
        //Arrange
        CartItem secondCartItem = mock(CartItem.class);
        Product secondProduct = mock(Product.class);

        when(account.getAccountId()).thenReturn("ACC-1");
        when(cart.isEmpty()).thenReturn(false);
        when(cart.getAccountId()).thenReturn("ACC-1");

        List<CartItem> cartItems = List.of(cartItem, secondCartItem);
        when(cart.getItems()).thenReturn(cartItems);

        when(cartItem.getQuantity()).thenReturn(2);
        when(secondCartItem.getQuantity()).thenReturn(5);

        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        when(secondProduct.getPrice()).thenReturn(new BigDecimal("10.00"));

        when(productInventoryService.reserveStock(cartItems))
                .thenReturn(List.of(product, secondProduct));

        when(pricingService.calculateFinalPrice(anyList()))
                .thenReturn(new BigDecimal("225.00"));

        when(invoiceGenerator.generate(any(Order.class))).thenReturn(invoice);

        //Act
        Order result = orderProcessor.processCheckout(account, cart);

        //Assert
        assertThat(result.getItems())
                .hasSize(2);

        assertThat(result.getTotalPrice())
                .isEqualByComparingTo("225.00");

        verify(pricingService).calculateFinalPrice(anyList());
        verify(productInventoryService).reserveStock(cartItems);
    }

    @Test
    void shouldNotContinueCheckoutWhenStockReservationFails() {
        //Arrange
        when(account.getAccountId()).thenReturn("ACC-1");
        when(cart.isEmpty()).thenReturn(false);
        when(cart.getAccountId()).thenReturn("ACC-1");

        List<CartItem> cartItems = List.of(cartItem);
        when(cart.getItems()).thenReturn(cartItems);

        when(productInventoryService.reserveStock(cartItems))
                .thenThrow(new ProductUnavailableException("PROD-1", 12, 10));

        //Act + Assert
        assertThatExceptionOfType(ProductUnavailableException.class)
                .isThrownBy(() -> orderProcessor.processCheckout(account, cart));

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(invoiceRepository);
        verifyNoInteractions(pricingService);
        verify(invoiceGenerator, never()).generate(any());
        verify(cart, never()).clear();
    }

    @Test
    void shouldThrowExceptionWhenProductIsUnavailable() {
        //Arrange
        when(account.getAccountId()).thenReturn("ACC-1");

        when(cart.isEmpty()).thenReturn(false);
        when(cart.getAccountId()).thenReturn("ACC-1");

        List<CartItem> cartItems = List.of(cartItem);
        when(cart.getItems()).thenReturn(cartItems);


        when(productInventoryService.reserveStock(cartItems))
                .thenThrow(new ProductUnavailableException("PROD-1", 12, 10));

        //Act + Assert
        assertThatExceptionOfType(ProductUnavailableException.class)
                .isThrownBy(() -> orderProcessor.processCheckout(account, cart));

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(invoiceRepository);
        verify(invoiceGenerator, never()).generate(any());
        verify(cart, never()).clear();
    }

    @Test
    void shouldNotModifyStockWhenProductIsUnavailable() {
        //Arrange
        when(account.getAccountId()).thenReturn("ACC-1");

        when(cart.isEmpty()).thenReturn(false);
        when(cart.getAccountId()).thenReturn("ACC-1");

        List<CartItem> cartItems = List.of(cartItem);
        when(cart.getItems()).thenReturn(cartItems);

        when(productInventoryService.reserveStock(cartItems))
                .thenThrow(new ProductUnavailableException("PROD-1", 12, 10));

        //Act + Assert
        assertThatExceptionOfType(ProductUnavailableException.class)
                .isThrownBy(() -> orderProcessor.processCheckout(account, cart));

        verifyNoInteractions(pricingService);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(invoiceRepository);
        verify(productInventoryService, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void shouldExecuteInCorrectOrder() {
        //Arrange
        prepareValidOrder();

        when(productInventoryService.reserveStock(anyList()))
                .thenReturn(List.of(product));

        when(pricingService.calculateFinalPrice(anyList())).thenReturn(new BigDecimal("200.00"));

        when(invoiceGenerator.generate(any(Order.class))).thenReturn(invoice);

        //Act
        orderProcessor.processCheckout(account, cart);

        //Assert
        InOrder inOrder = inOrder(
                productInventoryService,
                pricingService,
                orderRepository,
                invoiceGenerator,
                invoiceRepository,
                cart
        );

        inOrder.verify(productInventoryService).reserveStock(List.of(cartItem));
        inOrder.verify(pricingService).calculateFinalPrice(anyList());
        inOrder.verify(orderRepository).add(any(Order.class));
        inOrder.verify(invoiceGenerator).generate(any(Order.class));
        inOrder.verify(invoiceRepository).add(invoice);
        inOrder.verify(cart).clear();

        inOrder.verifyNoMoreInteractions();
    }

    private void prepareValidOrder() {
        when(account.getAccountId()).thenReturn("ACC-1");

        when(cart.isEmpty()).thenReturn(false);
        when(cart.getAccountId()).thenReturn("ACC-1");

        List<CartItem> cartItems = List.of(cartItem);
        when(cart.getItems()).thenReturn(cartItems);

        when(cartItem.getQuantity()).thenReturn(2);

        when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
    }
}
