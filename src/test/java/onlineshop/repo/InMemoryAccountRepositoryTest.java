package onlineshop.repo;

import onlineshop.domain.useraccount.Account;
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
                .email("john@gmail.com")
                .build();
    }

    @Test
    void shouldAddPutAnAccountToTheRepository() {
        accountRepository.add(account);

        assertThat(accountRepository.findAll())
                .singleElement()
                .isEqualTo(account);

        assertThat(accountRepository.findById("1"))
                .isPresent()
                .contains(account);
    }

    @Test
    void shouldRejectDuplicateAccountId() {
        Account accountDuplicate = Account.builder()
                .accountId("1")
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .build();

        accountRepository.add(account);

        assertThatThrownBy(() -> accountRepository.add(accountDuplicate))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessage("Account with id " + account.getAccountId() + " already exists");

        assertThat(accountRepository.findById(account.getAccountId()))
                .contains(account);
    }

    @Test
    void shouldFindAccountById() {
        accountRepository.add(account);

        assertThat(accountRepository.findById(account.getAccountId()))
                .contains(account);
    }

    @Test
    void shouldFindAllAccounts() {
        Account secondAccount = Account.builder()
                .accountId("2")
                .firstName("Mark")
                .lastName("John")
                .email("mark@gmail.com")
                .build();

        accountRepository.add(account);
        accountRepository.add(secondAccount);

        assertThat(accountRepository.findAll())
                .contains(account)
                .contains(secondAccount);
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        assertThat(accountRepository.findById("UNKNOWN"))
                .isEmpty();
    }
}
