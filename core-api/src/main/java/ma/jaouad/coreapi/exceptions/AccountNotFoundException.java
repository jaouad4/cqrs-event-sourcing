package ma.jaouad.coreapi.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountId) {
        super(String.format("Account not found with ID: %s", accountId));
    }
}
