package onlineshop.repo;

import onlineshop.domain.product.Computer;
import onlineshop.domain.product.Smartphone;
import onlineshop.exception.ProductAlreadyExistsException;
import onlineshop.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class InMemoryProductRepositoryTest {
    Computer computer;
    Smartphone smartphone;
    InMemoryProductRepository repository;

    @BeforeEach
    void productsSetup() {
        computer = Computer.builder()
                .id("1")
                .name("testComputer")
                .price(new BigDecimal("10"))
                .quantity(1)
                .cpu("Intel")
                .ram("64 GB")
                .build();

        smartphone = Smartphone.builder()
                .id("1")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("charger")
                .accessory("case")
                .build();

        repository = new InMemoryProductRepository();
    }

    @Test
    void shouldAddMethodPutAnObjectToTheRepository() {
        //Act
        repository.add(computer);

        //Assert
        assertThat(repository.findAll())
                .contains(computer);

        assertThat(repository.findById("1"))
                .isPresent()
                .contains(computer);
    }

    @Test
    void shouldAddNotReplaceExistingProductIdAndThrowException() {
        //Arrange
        Computer testDuplicate = Computer.builder()
                .id("1")
                .name("duplicate")
                .price(new BigDecimal("300"))
                .quantity(20)
                .cpu("AMD")
                .ram("128 GB")
                .build();

        //Act
        repository.add(computer);

        //Assert
        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> repository.add(testDuplicate));

        assertThatThrownBy(() -> repository.add(testDuplicate))
                .hasMessage("Product with id 1 already exists");

        assertThat(repository.findById("1"))
                .contains(computer);
    }

    @Test
    void shouldDeleteExistingProduct() {
        //Act
        repository.add(computer);
        boolean result = repository.delete("1");

        //Assert
        assertThat(result).isTrue();
        assertThat(repository.findById("1"))
                .isEmpty();
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingProduct() {
        //Act
        boolean result = repository.delete("1");

        //Assert
        assertThat(result).isFalse();
        assertThat(repository.findById("1"))
                .isEmpty();
    }

    @Test
    void shouldUpdateProduct() {
        //Act
        repository.add(computer);
        repository.updateProduct("1", smartphone);

        //Assert
        assertThat(repository.findById("1"))
                .contains(smartphone);
    }

    @Test
    void shouldThrowExceptionWhenChangingSpecificationWithNonMatchingIds() {
        //Arrange
        Smartphone smartphone = Smartphone.builder()
                .id("2")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("charger")
                .accessory("case")
                .build();

        //Act
        repository.add(computer);

        //Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> repository.updateProduct("1", smartphone));

        assertThatThrownBy(() -> repository.updateProduct("1", smartphone))
                .hasMessage("Product ids do not match");
    }

    @Test
    void shouldThrowExceptionWhenChangingSpecificationOfNonExistingId() {
        //Assert
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> repository.updateProduct("1", computer));

        assertThatThrownBy(() -> repository.updateProduct("1", computer))
                .hasMessage("Product with id 1 not found");
    }

    @Test
    void shouldFindAllProducts() {
        //Arrange
        Smartphone smartphone = Smartphone.builder()
                .id("2")
                .name("testSmartphone")
                .price(new BigDecimal("10"))
                .quantity(1)
                .color("yellow")
                .batteryCapacity("1050 mAh")
                .accessory("charger")
                .accessory("case")
                .build();

        //Act
        repository.add(computer);
        repository.add(smartphone);

        //Assert
        assertThat(repository.findAll())
                .contains(computer)
                .contains(smartphone);
    }
}
