package onlineshop.repo;

import onlineshop.domain.useraccount.Account;
import onlineshop.domain.useraccount.Email;
import onlineshop.exception.AccountAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InMemoryAccountRepositoryTest {
    Account account;
    AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        accountRepository = new InMemoryAccountRepository();

        account = Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@gmail.com"))
                .build();
    }

    @Test
    void shouldAddPutAnAccountToTheRepository() {
        //Act
        accountRepository.add(account);

        //Assert
        assertThat(accountRepository.findAll())
                .singleElement()
                .isEqualTo(account);

        assertThat(accountRepository.findById("1"))
                .isPresent()
                .contains(account);
    }

    @Test
    void shouldRejectDuplicateAccountId() {
        //Arrange
        Account accountDuplicate = Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@gmail.com"))
                .build();

        //Act
        accountRepository.add(account);

        //Assert
        assertThatThrownBy(() -> accountRepository.add(accountDuplicate))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessage("Account with id " + account.getAccountId() + " already exists");

        assertThat(accountRepository.findById(account.getAccountId()))
                .contains(account);
    }

    @Test
    void shouldFindAccountById() {
        //Act
        accountRepository.add(account);

        //Assert
        assertThat(accountRepository.findById(account.getAccountId()))
                .contains(account);
    }

    @Test
    void shouldFindAllAccounts() {
        //Arrange
        Account secondAccount = Account.builder()
                .accountId("2")
                .firstName("Mark")
                .lastName("John")
                .email(new Email("john@gmail.com"))
                .build();

        //Act
        accountRepository.add(account);
        accountRepository.add(secondAccount);

        //Assert
        assertThat(accountRepository.findAll())
                .contains(account)
                .contains(secondAccount);
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        //Assert
        assertThat(accountRepository.findById("UNKNOWN"))
                .isEmpty();
    }
}
