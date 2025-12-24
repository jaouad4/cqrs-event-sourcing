package ma.jaouad.accountservice.queries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.jaouad.coreapi.enums.OperationType;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour la réponse de consultation d'une opération
 * 
 * Ce DTO est utilisé pour la sérialisation/désérialisation via Axon Server.
 * Il évite les problèmes de conversion LinkedHashMap rencontrés avec les entités JPA
 * lors de l'utilisation de ResponseTypes.multipleInstancesOf().
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO implements Serializable {
    private Long id;
    private Instant date;
    private double amount;
    private OperationType type;
    private String accountId;
}
