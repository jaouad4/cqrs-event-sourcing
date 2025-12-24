package ma.jaouad.coreapi.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Query pour récupérer l'historique des opérations d'un compte
 * 
 * Les annotations Jackson (@JsonCreator, @JsonProperty) permettent
 * la désérialisation correcte lors du passage via Axon Server
 */
@Getter
@NoArgsConstructor
public class GetAccountOperationsQuery implements Serializable {
    @JsonProperty("accountId")
    private String accountId;
    
    @JsonCreator
    public GetAccountOperationsQuery(@JsonProperty("accountId") String accountId) {
        this.accountId = accountId;
    }
}
