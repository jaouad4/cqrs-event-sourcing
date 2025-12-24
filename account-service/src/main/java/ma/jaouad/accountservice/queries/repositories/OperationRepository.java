package ma.jaouad.accountservice.queries.repositories;

import ma.jaouad.accountservice.queries.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByAccountId(String accountId);
}
