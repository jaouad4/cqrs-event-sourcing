package ma.jaouad.accountservice.queries.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.jaouad.coreapi.enums.AccountStatus;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private String id;
    private Instant createdAt;
    private double balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String currency;
    
    /**
     * Collection des opérations liées au compte
     * @JsonIgnore empêche la sérialisation de cette collection et évite:
     * - LazyInitializationException (accès hors session Hibernate)
     * - Références circulaires lors de la sérialisation JSON
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Operation> operations;
}
