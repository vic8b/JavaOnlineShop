package onlineshop.repo;

import onlineshop.domain.useraccount.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    void add(Account account);
    Optional<Account> findById(String id);
    List<Account> findAll();
}
