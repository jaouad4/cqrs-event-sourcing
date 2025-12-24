package ma.jaouad.coreapi.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.jaouad.coreapi.enums.AccountStatus;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Commande pour modifier le statut d'un compte
 * 
 * Les annotations Jackson (@JsonCreator, @JsonProperty) permettent
 * la désérialisation correcte lors du passage via Axon Server
 */
@Getter
@NoArgsConstructor
public class UpdateAccountStatusCommand {
    @TargetAggregateIdentifier
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("accountStatus")
    private AccountStatus accountStatus;
    
    @JsonCreator
    public UpdateAccountStatusCommand(
            @JsonProperty("id") String id,
            @JsonProperty("accountStatus") AccountStatus accountStatus) {
        this.id = id;
        this.accountStatus = accountStatus;
    }
}
