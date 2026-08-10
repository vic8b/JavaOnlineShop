package onlineshop.domain.useraccount;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class Account {
    @NonNull
    private final String accountId;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final Email email;

    @Builder
    public Account(@NonNull String accountId, @NonNull String firstName, @NonNull String lastName, @NonNull Email email) {
        if (accountId.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
        if (firstName.isBlank()) throw new IllegalArgumentException("First name cannot be blank");
        if (lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be blank");

        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Account [" + accountId + "]"
                + "\nFirst name: " + firstName
                + "\nLast name: " + lastName
                + "\nEmail: " + email;
    }
}
