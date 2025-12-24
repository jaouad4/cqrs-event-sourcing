package ma.jaouad.coreapi.events;

import lombok.Getter;
import ma.jaouad.coreapi.enums.AccountStatus;

/**
 * Event emis lors de la creation d'un compte
 * 
 * Converti de record en classe normale pour compatibilite XStream 1.4.21
 * XStream ne supporte les records qu'a partir de la version 1.5.0+
 */
@Getter
public class AccountCreatedEvent {
    private final String accountId;
    private final double initialBalance;
    private final String currency;
    private final AccountStatus accountStatus;
    
    public AccountCreatedEvent(String accountId, double initialBalance, 
                              String currency, AccountStatus accountStatus) {
        this.accountId = accountId;
        this.initialBalance = initialBalance;
        this.currency = currency;
        this.accountStatus = accountStatus;
    }
}
