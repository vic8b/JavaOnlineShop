package onlineshop.service.product;

import onlineshop.domain.cart.CartItem;
import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Product;
import onlineshop.exception.ProductNotFoundException;
import onlineshop.exception.ProductUnavailableException;
import onlineshop.repo.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceTest {
    Computer testComputer;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductInventoryService productInventoryService;

    @BeforeEach
    void setup() {
        testComputer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();
    }

    @Test
    void shouldSuccessfullyAddProductToRepo() {
        //Act
        productInventoryService.addProduct(testComputer);

        //Assert
        verify(productRepository).add(testComputer);
    }

    @Test
    void shouldSuccessfullyDeleteProductFromRepo() {
        //Arrange
        when(productRepository.delete("1")).thenReturn(true);

        //Act
        boolean result = productInventoryService.deleteProduct("1");

        //Assert
        assertThat(result).isTrue();
        verify(productRepository).delete("1");
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingProduct() {
        //Arrange
        when(productRepository.delete("1")).thenReturn(false);

        //Act
        boolean result = productInventoryService.deleteProduct("1");

        //Assert
        assertThat(result).isFalse();
        verify(productRepository).delete("1");
    }

    @Test
    void shouldSuccessfullyUpdateProduct() {
        //Act
        productInventoryService.updateProduct("1", testComputer);

        //Assert
        verify(productRepository).updateProduct("1", testComputer);
    }

    @Test
    void shouldSuccessfullyFindProductById() {
        //Arrange
        when(productRepository.findById("1"))
                .thenReturn(Optional.ofNullable(testComputer));

        //Act
        Product productById = productInventoryService.findProductById("1");

        //Assert
        assertThat(productById)
                .isSameAs(testComputer);
    }

    @Test
    void shouldSuccessfullyFindAllProducts() {
        //Arrange
        when(productRepository.findAll())
                .thenReturn(List.of(testComputer));

        //Act
        List<Product> allProducts = productInventoryService.findAllProducts();

        //Assert
        assertThat(allProducts)
                .containsExactly(testComputer);
    }

    @Test
    void shouldThrowExceptionWhenProductIdNotFound() {
        //Act + Assert
        assertThatThrownBy(() -> productInventoryService.findProductById("1"))
                .hasMessage("Product with id 1 not found");
    }

    @Test
    void shouldIncreaseProductQuantity() {
        //Arrange
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));

        //Act
        productInventoryService.increaseStock("1", 1);

        //Assert
        assertThat(testComputer.getQuantity())
                .isEqualTo(2);
        verify(productRepository).findById("1");
    }

    @Test
    void shouldDecreaseProductQuantity() {
        //Arrange
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));

        //Act
        productInventoryService.decreaseStock("1", 1);

        //Assert
        assertThat(testComputer.getQuantity())
                .isEqualTo(0);
        verify(productRepository).findById("1");
    }

    @Test
    void shouldChangeProductPrice() {
        //Arrange
        BigDecimal newPrice = new BigDecimal("5");
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));

        //Act
        productInventoryService.changePrice("1", newPrice);

        //Assert
        assertThat(testComputer.getPrice())
                .isEqualTo(newPrice.setScale(2, RoundingMode.HALF_UP));
        verify(productRepository).findById("1");
    }

    @Test
    void shouldThrowExceptionWhenNewPriceIsNotPositive() {
        //Arrange
        BigDecimal newPrice = new BigDecimal("-10");
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));

        //Act + Assert
        assertThatThrownBy(() -> productInventoryService.changePrice("1", newPrice))
                .hasMessage("Update price must be positive");
        verify(productRepository).findById("1");
    }

    @Test
    void shouldSuccessfullyReserveStockForAllProducts() {
        //Arrange
        Computer secondComputer = Computer.builder()
                .id("2")
                .name("secondComputer")
                .price(new BigDecimal("20"))
                .quantity(5)
                .cpu("AMD")
                .ram("32 GB")
                .build();

        CartItem firstCartItem = mock(CartItem.class);
        CartItem secondCartItem = mock(CartItem.class);

        when(firstCartItem.getProduct()).thenReturn(testComputer);
        when(firstCartItem.getQuantity()).thenReturn(1);

        when(secondCartItem.getProduct()).thenReturn(secondComputer);
        when(secondCartItem.getQuantity()).thenReturn(2);

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));
        when(productRepository.findById("2"))
                .thenReturn(Optional.of(secondComputer));

        //Act
        List<Product> reservedProducts = productInventoryService.reserveStock(List.of(firstCartItem, secondCartItem));

        //Assert
        assertThat(reservedProducts).containsExactly(testComputer, secondComputer);

        assertThat(testComputer.getQuantity()).isZero();

        assertThat(secondComputer.getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldNotDecreaseAnyStockWhenOneProductIsUnavailable() {
        //Arrange
        Computer firstComputer = Computer.builder()
                .id("1")
                .name("firstComputer")
                .price(new BigDecimal("10"))
                .quantity(5)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        Computer secondComputer = Computer.builder()
                .id("2")
                .name("secondComputer")
                .price(new BigDecimal("20"))
                .quantity(1)
                .cpu("AMD")
                .ram("32 GB")
                .build();

        CartItem firstCartItem = mock(CartItem.class);
        CartItem secondCartItem = mock(CartItem.class);

        when(firstCartItem.getProduct()).thenReturn(firstComputer);
        when(firstCartItem.getQuantity()).thenReturn(1);

        when(secondCartItem.getProduct()).thenReturn(secondComputer);
        when(secondCartItem.getQuantity()).thenReturn(2);

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(firstComputer));
        when(productRepository.findById("2"))
                .thenReturn(Optional.of(secondComputer));

        //Act + Assert
        assertThatThrownBy(() -> productInventoryService.reserveStock(List.of(firstCartItem, secondCartItem)))
                .isInstanceOf(ProductUnavailableException.class);

        assertThat(firstComputer.getQuantity()).isEqualTo(5);
        assertThat(secondComputer.getQuantity()).isEqualTo(1);
    }

    @Test
    void shouldNotDecreaseStockWhenProductIsNotFoundDuringReservation() {
        //Arrange
        Computer firstComputer = Computer.builder()
                .id("1")
                .name("firstComputer")
                .price(new BigDecimal("10"))
                .quantity(5)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        Computer secondComputer = Computer.builder()
                .id("2")
                .name("secondComputer")
                .price(new BigDecimal("20"))
                .quantity(5)
                .cpu("AMD")
                .ram("32 GB")
                .build();

        CartItem firstCartItem = mock(CartItem.class);
        CartItem secondCartItem = mock(CartItem.class);

        when(firstCartItem.getProduct()).thenReturn(firstComputer);
        when(firstCartItem.getQuantity()).thenReturn(2);

        when(secondCartItem.getProduct()).thenReturn(secondComputer);

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(firstComputer));
        when(productRepository.findById("2"))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThatThrownBy(() -> productInventoryService.reserveStock(List.of(firstCartItem, secondCartItem)))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product with id 2 not found");

        assertThat(firstComputer.getQuantity())
                .isEqualTo(5);
    }
}
