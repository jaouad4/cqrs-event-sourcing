package ma.jaouad.coreapi.events;

import lombok.Getter;
import ma.jaouad.coreapi.enums.AccountStatus;

/**
 * Event emis lors du changement de statut d'un compte
 * 
 * Converti de record en classe normale pour compatibilite XStream 1.4.21
 * XStream ne supporte les records qu'a partir de la version 1.5.0+
 */
@Getter
public class AccountStatusUpdatedEvent {
    private final String accountId;
    private final AccountStatus fromStatus;
    private final AccountStatus toStatus;
    
    public AccountStatusUpdatedEvent(String accountId, AccountStatus fromStatus, 
                                    AccountStatus toStatus) {
        this.accountId = accountId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }
}
