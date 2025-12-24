package ma.jaouad.analyticsservice.queries.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.jaouad.coreapi.enums.AccountStatus;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountView {
    @Id
    private String id;
    private Instant createdAt;
    private double balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String currency;
    private int operationCount;
}
