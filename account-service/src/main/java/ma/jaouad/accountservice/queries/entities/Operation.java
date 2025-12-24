package ma.jaouad.accountservice.queries.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ma.jaouad.coreapi.enums.OperationType;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant date;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    
    /**
     * Référence vers le compte associé
     * @JsonIgnore empêche la sérialisation de cette référence pour éviter
     * les références circulaires (Account -> operations -> account -> ...)
     */
    @ManyToOne
    @JsonIgnore
    private Account account;
}
