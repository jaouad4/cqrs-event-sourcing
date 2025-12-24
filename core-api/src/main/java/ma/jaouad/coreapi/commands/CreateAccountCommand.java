package ma.jaouad.coreapi.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Commande pour créer un nouveau compte
 * 
 * Les annotations Jackson (@JsonCreator, @JsonProperty) permettent
 * la désérialisation correcte lors du passage via Axon Server
 */
@Getter
@NoArgsConstructor
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("initialBalance")
    private double initialBalance;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonCreator
    public CreateAccountCommand(
            @JsonProperty("id") String id,
            @JsonProperty("initialBalance") double initialBalance,
            @JsonProperty("currency") String currency) {
        this.id = id;
        this.initialBalance = initialBalance;
        this.currency = currency;
    }
}
