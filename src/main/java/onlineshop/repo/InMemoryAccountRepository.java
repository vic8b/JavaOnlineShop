package onlineshop.repo;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import onlineshop.domain.useraccount.Account;
import onlineshop.exception.AccountAlreadyExistsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accountRepo = new HashMap<>();

    @Override
    public void add(@NonNull Account account) {
        if (accountRepo.putIfAbsent(account.getAccountId(), account) != null) {
            throw new AccountAlreadyExistsException(account.getAccountId());
        }

        log.info("Account {} has been added to the repository", account.getAccountId());
    }

    @Override
    public Optional<@NonNull Account> findById(String id) {
        return accountRepo.values().stream()
                .filter(account -> account.getAccountId().equals(id))
                .findFirst();
    }

    @Override
    public List<Account> findAll() {
        return List.copyOf(accountRepo.values());
    }
}
