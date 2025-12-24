package ma.jaouad.coreapi.exceptions;

import ma.jaouad.coreapi.enums.AccountStatus;

public class InvalidAccountStatusException extends RuntimeException {
    public InvalidAccountStatusException(String accountId, AccountStatus currentStatus, String operation) {
        super(String.format("Cannot perform %s on account %s. Current status: %s. Account must be ACTIVATED.",
                operation, accountId, currentStatus));
    }
}
