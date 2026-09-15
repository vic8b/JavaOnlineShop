package onlineshop.domain.useraccount;

import lombok.NonNull;

import java.util.regex.Pattern;

public record Email(@NonNull String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    public Email {
        if (!isPatternValid(value)) throw new IllegalArgumentException("Email is incorrect");
    }

    private static boolean isPatternValid(String email) {
        return EMAIL_PATTERN
                .matcher(email)
                .matches();
    }

    @Override
    public String toString() {
        return value;
    }
}
