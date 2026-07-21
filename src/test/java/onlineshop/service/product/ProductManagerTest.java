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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductManagerTest {
    Computer testComputer;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductManager productManager;

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
        productManager.addProduct(testComputer);

        //Assert
        verify(productRepository).add(testComputer);
    }

    @Test
    void shouldSuccessfullyDeleteProductFromRepo() {
        //Arrange
        when(productRepository.delete("1")).thenReturn(true);

        //Act
        boolean result = productManager.deleteProduct("1");

        //Assert
        assertThat(result).isTrue();
        verify(productRepository).delete("1");
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingProduct() {
        //Arrange
        when(productRepository.delete("1")).thenReturn(false);

        //Act
        boolean result = productManager.deleteProduct("1");

        //Assert
        assertThat(result).isFalse();
        verify(productRepository).delete("1");
    }

    @Test
    void shouldSuccessfullyUpdateProduct() {
        //Act
        productManager.updateProduct("1", testComputer);

        //Assert
        verify(productRepository).changeSpecification("1", testComputer);
    }

    @Test
    void shouldSuccessfullyFindProductById() {
        //Arrange
        when(productRepository.findById("1"))
                .thenReturn(Optional.ofNullable(testComputer));

        //Act
        Product productById = productManager.findProductById("1");

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
        List<Product> allProducts = productManager.findAllProducts();

        //Assert
        assertThat(allProducts)
                .containsExactly(testComputer);
    }

    @Test
    void shouldThrowExceptionWhenProductIdNotFound() {
        //Act + Assert
        assertThatThrownBy(() -> productManager.findProductById("1"))
                .hasMessage("Product with id 1 not found");
    }

    @Test
    void shouldIncreaseProductQuantity() {
        //Arrange
        when(productRepository.findById("1"))
                .thenReturn(Optional.of(testComputer));

        //Act
        productManager.increaseStock("1", 1);

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
        productManager.decreaseStock("1", 1);

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
        productManager.changePrice("1", newPrice);

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
        assertThatThrownBy(() -> productManager.changePrice("1", newPrice))
                .hasMessage("Update price must be positive");
        verify(productRepository).findById("1");
    }
}
