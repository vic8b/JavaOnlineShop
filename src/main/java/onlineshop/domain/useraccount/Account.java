package onlineshop.domain.useraccount;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Account {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    @NonNull
    private final String accountId;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final String email;

    @Builder
    public Account(@NonNull String accountId, @NonNull String firstName, @NonNull String lastName, @NonNull String email) {
        if (!patternMatches(email)) throw new IllegalArgumentException("Email is incorrect");
        if (accountId.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
        if (firstName.isBlank()) throw new IllegalArgumentException("First name cannot be blank");
        if (lastName.isBlank()) throw new IllegalArgumentException("Last name cannot be blank");

        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    private static boolean patternMatches(String email) {
        return EMAIL_PATTERN
                .matcher(email)
                .matches();
    }

    @Override
    public String toString() {
        return "Account [" + accountId + "]"
                + "\nFirst name: " + firstName
                + "\nLast name: " + lastName
                + "\nEmail: " + email;
    }
}
