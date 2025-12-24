package ma.jaouad.coreapi.events;

import lombok.Getter;

/**
 * Event emis lors du debit d'un compte
 * 
 * Converti de record en classe normale pour compatibilite XStream 1.4.21
 * XStream ne supporte les records qu'a partir de la version 1.5.0+
 */
@Getter
public class AccountDebitedEvent {
    private final String accountId;
    private final double amount;
    
    public AccountDebitedEvent(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
