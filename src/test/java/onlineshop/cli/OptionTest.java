package onlineshop.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OptionTest {
    @Test
    void shouldReturnOptionForValidNumber() {
        Option option = Option.fromNumber(1);

        assertThat(option).isEqualTo(Option.SHOW_PRODUCTS);
    }

    @Test
    void shouldReturnNullForInvalidNumber() {
        Option option = Option.fromNumber(999);

        assertThat(option).isNull();
    }
}
