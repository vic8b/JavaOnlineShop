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
        repository.add(computer);

        assertThat(repository.findAll())
                .contains(computer);

        assertThat(repository.findById("1"))
                .isPresent()
                .contains(computer);
    }

    @Test
    void shouldAddNotReplaceExistingProductIdAndThrowException() {
        Computer testDuplicate = Computer.builder()
                .id("1")
                .name("duplicate")
                .price(new BigDecimal("300"))
                .quantity(20)
                .cpu("AMD")
                .ram("128 GB")
                .build();

        repository.add(computer);

        assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> repository.add(testDuplicate));

        assertThatThrownBy(() -> repository.add(testDuplicate))
                .hasMessage("Product with id 1 already exists");

        assertThat(repository.findById("1"))
                .contains(computer);
    }

    @Test
    void shouldDeleteExistingProduct() {
        repository.add(computer);
        boolean result = repository.delete("1");

        assertThat(result).isTrue();
        assertThat(repository.findById("1"))
                .isEmpty();
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingProduct() {
        boolean result = repository.delete("1");

        assertThat(result).isFalse();
        assertThat(repository.findById("1"))
                .isEmpty();
    }

    @Test
    void shouldChangeSpecification() {
        repository.add(computer);
        repository.changeSpecification("1", smartphone);

        assertThat(repository.findById("1"))
                .contains(smartphone);
    }

    @Test
    void shouldThrowExceptionWhenChangingSpecificationWithNonMatchingIds() {
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

        repository.add(computer);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> repository.changeSpecification("1", smartphone));

        assertThatThrownBy(() -> repository.changeSpecification("1", smartphone))
                .hasMessage("Product ids do not match");
    }

    @Test
    void shouldThrowExceptionWhenChangingSpecificationOfNonExistingId() {
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> repository.changeSpecification("1", computer));

        assertThatThrownBy(() -> repository.changeSpecification("1", computer))
                .hasMessage("Product with id 1 not found");
    }

    @Test
    void shouldFindAllReturnAllProducts() {
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

        repository.add(computer);
        repository.add(smartphone);

        assertThat(repository.findAll())
                .contains(computer)
                .contains(smartphone);
    }
}
