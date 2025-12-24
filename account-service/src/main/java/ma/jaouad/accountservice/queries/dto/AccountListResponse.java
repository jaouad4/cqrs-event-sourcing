package ma.jaouad.accountservice.queries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper pour la réponse contenant une liste de comptes
 * 
 * Ce wrapper résout les problèmes de sérialisation de List<AccountDTO>
 * avec Axon Server en encapsulant la liste dans un objet.
 * Axon Server sérialise mieux les objets que les collections directes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponse implements Serializable {
    private List<AccountDTO> accounts;
}
