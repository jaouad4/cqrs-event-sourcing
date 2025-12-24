package ma.jaouad.accountservice.queries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.jaouad.coreapi.enums.AccountStatus;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour la réponse de consultation d'un compte
 * 
 * Ce DTO est utilisé pour la sérialisation/désérialisation via Axon Server.
 * Contrairement aux entités JPA, les DTOs sont optimisés pour le transfert
 * de données et évitent les problèmes de LazyInitializationException.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO implements Serializable {
    private String id;
    private Instant createdAt;
    private double balance;
    private AccountStatus status;
    private String currency;
}
