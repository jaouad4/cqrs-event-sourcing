package ma.jaouad.analyticsservice.queries.repositories;

import ma.jaouad.analyticsservice.queries.entities.OperationView;
import ma.jaouad.coreapi.enums.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OperationViewRepository extends JpaRepository<OperationView, Long> {
    
    @Query("SELECT COUNT(o) FROM OperationView o WHERE o.type = ?1")
    Long countByType(OperationType type);
    
    @Query("SELECT SUM(o.amount) FROM OperationView o WHERE o.type = ?1")
    Double sumAmountByType(OperationType type);
}
