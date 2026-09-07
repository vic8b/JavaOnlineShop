package onlineshop.domain.useraccount;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {
    @Test
    void shouldCreateValidEmail() {
        //Arrange
        Email email = new Email("john@gmail.com");

        //Assert
        assertThat(email.value())
                .isEqualTo("john@gmail.com");
    }

    @Test
    void shouldIncorrectEmailThrowException() {
        //Act + Assert
        assertThatThrownBy(() -> new Email("johngmail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is incorrect");
    }

    @Test
    void shouldBlankEmailThrowException() {
        //Act + Assert
        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is incorrect");
    }
}
