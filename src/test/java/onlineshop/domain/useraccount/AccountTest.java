package onlineshop.domain.useraccount;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AccountTest {
    @Test
    void shouldBuilderCreateInstance() {
        Account account = Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@gmail.com"))
                .build();

        assertThat(account)
                .hasFieldOrPropertyWithValue("accountId", "1")
                .hasFieldOrPropertyWithValue("firstName", "John")
                .hasFieldOrPropertyWithValue("lastName", "Doe")
                .hasFieldOrPropertyWithValue("email", new Email("john@gmail.com"));
    }

    @Test
    void shouldIncorrectEmailThrowException() {
        assertThatThrownBy(() -> Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john_gmail.com"))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is incorrect");

        assertThatThrownBy(() -> Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@gmail_com"))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is incorrect");

        assertThatThrownBy(() -> Account.builder()
                .accountId("1")
                .firstName("  ")
                .lastName("  ")
                .email(new Email("john@gmail.com"))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name cannot be blank");
    }

    @Test
    void shouldBlankIdThrowException() {
        assertThatThrownBy(() -> Account.builder()
                .accountId("")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@gmail.com"))
                .build())
                .hasMessage("ID cannot be blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Account.builder()
                        .accountId("")
                        .firstName("John")
                        .lastName("Doe")
                        .email(new Email("john@gmail.com"))
                        .build());
    }

    @Test
    void shouldBlankFirstNameThrowException() {
        assertThatThrownBy(() -> Account.builder()
                .accountId("1")
                .firstName("")
                .lastName("Doe")
                .email(new Email("john@gmail.com"))
                .build())
                .hasMessage("First name cannot be blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Account.builder()
                        .accountId("1")
                        .firstName("")
                        .lastName("Doe")
                        .email(new Email("john@gmail.com"))
                        .build());
    }

    @Test
    void shouldBlankLastNameThrowException() {
        assertThatThrownBy(() -> Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("")
                .email(new Email("john@gmail.com"))
                .build())
                .hasMessage("Last name cannot be blank");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Account.builder()
                        .accountId("1")
                        .firstName("John")
                        .lastName("")
                        .email(new Email("john@gmail.com"))
                        .build());
    }
}
