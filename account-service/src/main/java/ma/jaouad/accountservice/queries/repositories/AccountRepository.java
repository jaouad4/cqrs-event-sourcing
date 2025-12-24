package ma.jaouad.accountservice.queries.repositories;

import ma.jaouad.accountservice.queries.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
