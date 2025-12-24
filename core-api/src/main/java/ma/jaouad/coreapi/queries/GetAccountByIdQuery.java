package ma.jaouad.coreapi.queries;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Query pour récupérer un compte par son ID
 * 
 * Les annotations Jackson (@JsonCreator, @JsonProperty) permettent
 * la désérialisation correcte lors du passage via Axon Server
 */
@Getter
@NoArgsConstructor
public class GetAccountByIdQuery implements Serializable {
    @JsonProperty("accountId")
    private String accountId;
    
    @JsonCreator
    public GetAccountByIdQuery(@JsonProperty("accountId") String accountId) {
        this.accountId = accountId;
    }
}
