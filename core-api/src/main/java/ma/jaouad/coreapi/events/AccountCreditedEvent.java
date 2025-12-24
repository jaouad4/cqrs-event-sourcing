package ma.jaouad.coreapi.events;

import lombok.Getter;

/**
 * Event emis lors du credit d'un compte
 * 
 * Converti de record en classe normale pour compatibilite XStream 1.4.21
 * XStream ne supporte les records qu'a partir de la version 1.5.0+
 */
@Getter
public class AccountCreditedEvent {
    private final String accountId;
    private final double amount;
    
    public AccountCreditedEvent(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
