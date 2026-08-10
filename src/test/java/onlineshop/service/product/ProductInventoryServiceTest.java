package onlineshop.service.product;

import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Product;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        verify(productRepository).changeSpecification("1", testComputer);
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
}
