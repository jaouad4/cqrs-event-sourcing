package ma.jaouad.coreapi.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountId, double balance, double amount) {
        super(String.format("Insufficient balance for account %s. Current balance: %.2f, Requested amount: %.2f",
                accountId, balance, amount));
    }
}
