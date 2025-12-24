package ma.jaouad.analyticsservice.queries.repositories;

import ma.jaouad.analyticsservice.queries.entities.AccountView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountViewRepository extends JpaRepository<AccountView, String> {
    
    @Query("SELECT SUM(a.balance) FROM AccountView a")
    Double getTotalBalance();
    
    @Query("SELECT COUNT(a) FROM AccountView a")
    Long getTotalAccounts();
}
