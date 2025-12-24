package ma.jaouad.analyticsservice.queries.entities;

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
public class OperationView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountId;
    private Instant date;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
}
