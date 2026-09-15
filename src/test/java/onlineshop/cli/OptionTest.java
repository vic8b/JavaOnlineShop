package onlineshop.cli;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OptionTest {
    @Test
    void shouldReturnOptionForValidNumber() {
        //Act
        Optional<Option> option = Option.fromNumber(1);

        //Assert
        assertThat(option).contains(Option.SHOW_PRODUCTS);
    }

    @Test
    void shouldReturnNullForInvalidNumber() {
        //Act
        Optional<Option> option = Option.fromNumber(999);

        //Assert
        assertThat(option).isEmpty();
    }
}
