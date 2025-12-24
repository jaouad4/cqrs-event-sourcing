package ma.jaouad.coreapi.exceptions;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(double balance) {
        super(String.format("Initial balance cannot be negative: %.2f", balance));
    }
}
