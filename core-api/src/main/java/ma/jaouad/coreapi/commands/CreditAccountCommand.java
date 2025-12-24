package ma.jaouad.coreapi.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Commande pour créditer un compte
 * 
 * Les annotations Jackson (@JsonCreator, @JsonProperty) permettent
 * la désérialisation correcte lors du passage via Axon Server
 */
@Getter
@NoArgsConstructor
public class CreditAccountCommand {
    @TargetAggregateIdentifier
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("amount")
    private double amount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonCreator
    public CreditAccountCommand(
            @JsonProperty("id") String id,
            @JsonProperty("amount") double amount,
            @JsonProperty("currency") String currency) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }
}
