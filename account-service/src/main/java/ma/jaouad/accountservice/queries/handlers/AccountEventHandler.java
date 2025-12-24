package ma.jaouad.accountservice.queries.handlers;

import lombok.extern.slf4j.Slf4j;
import ma.jaouad.accountservice.exceptions.ErrorResponse;
import ma.jaouad.accountservice.queries.dto.AccountDTO;
import ma.jaouad.accountservice.queries.dto.AccountListResponse;
import ma.jaouad.accountservice.queries.dto.OperationDTO;
import ma.jaouad.accountservice.queries.dto.OperationListResponse;
import ma.jaouad.accountservice.queries.entities.Account;
import ma.jaouad.accountservice.queries.entities.Operation;
import ma.jaouad.accountservice.queries.repositories.AccountRepository;
import ma.jaouad.accountservice.queries.repositories.OperationRepository;
import ma.jaouad.coreapi.enums.OperationType;
import ma.jaouad.coreapi.events.AccountCreatedEvent;
import ma.jaouad.coreapi.events.AccountCreditedEvent;
import ma.jaouad.coreapi.events.AccountDebitedEvent;
import ma.jaouad.coreapi.events.AccountStatusUpdatedEvent;
import ma.jaouad.coreapi.exceptions.AccountNotFoundException;
import ma.jaouad.coreapi.queries.GetAccountByIdQuery;
import ma.jaouad.coreapi.queries.GetAccountOperationsQuery;
import ma.jaouad.coreapi.queries.GetAllAccountsQuery;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryExecutionException;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountEventHandler {
    
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountEventHandler(AccountRepository accountRepository, 
                              OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<?> eventMessage) {
        log.info("AccountCreatedEvent received");
        Account account = Account.builder()
            .id(event.getAccountId())
            .balance(event.getInitialBalance())
            .currency(event.getCurrency())
            .status(event.getAccountStatus())
            .createdAt(eventMessage.getTimestamp())
            .build();
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, EventMessage<?> eventMessage) {
        log.info("AccountCreditedEvent received");
        Account account = accountRepository.findById(event.getAccountId())
            .orElseThrow(() -> new AccountNotFoundException(event.getAccountId()));
        
        Operation operation = Operation.builder()
            .date(eventMessage.getTimestamp())
            .amount(event.getAmount())
            .type(OperationType.CREDIT)
            .account(account)
            .build();
        operationRepository.save(operation);
        
        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, EventMessage<?> eventMessage) {
        log.info("AccountDebitedEvent received");
        Account account = accountRepository.findById(event.getAccountId())
            .orElseThrow(() -> new AccountNotFoundException(event.getAccountId()));
        
        Operation operation = Operation.builder()
            .date(eventMessage.getTimestamp())
            .amount(event.getAmount())
            .type(OperationType.DEBIT)
            .account(account)
            .build();
        operationRepository.save(operation);
        
        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event, EventMessage<?> eventMessage) {
        log.info("AccountStatusUpdatedEvent received");
        Account account = accountRepository.findById(event.getAccountId())
            .orElseThrow(() -> new AccountNotFoundException(event.getAccountId()));
        
        account.setStatus(event.getToStatus());
        accountRepository.save(account);
    }

    @QueryHandler
    public AccountListResponse handle(GetAllAccountsQuery query) {
        log.info("GetAllAccountsQuery received");
        List<AccountDTO> accounts = accountRepository.findAll().stream()
            .map(this::convertToAccountDTO)
            .collect(Collectors.toList());
        return AccountListResponse.builder()
            .accounts(accounts)
            .build();
    }

    @QueryHandler
    public AccountDTO handle(GetAccountByIdQuery query) {
        log.info("GetAccountByIdQuery received for account: {}", query.getAccountId());
        try {
            Account account = accountRepository.findById(query.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(query.getAccountId()));
            return convertToAccountDTO(account);
        } catch (AccountNotFoundException ex) {
            // Créer un objet details sérialisable pour Axon Server
            ErrorResponse errorDetails = new ErrorResponse(
                java.time.Instant.now().toString(),
                404,
                "Not Found",
                ex.getMessage(),
                "/queries/accounts/" + query.getAccountId()
            );
            throw new QueryExecutionException(ex.getMessage(), ex, errorDetails);
        }
    }

    @QueryHandler
    public OperationListResponse handle(GetAccountOperationsQuery query) {
        log.info("GetAccountOperationsQuery received for account: {}", query.getAccountId());
        try {
            // Verifier que le compte existe
            accountRepository.findById(query.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(query.getAccountId()));
            
            List<OperationDTO> operations = operationRepository.findByAccountId(query.getAccountId()).stream()
                .map(this::convertToOperationDTO)
                .collect(Collectors.toList());
            return OperationListResponse.builder()
                .operations(operations)
                .build();
        } catch (AccountNotFoundException ex) {
            // Créer un objet details sérialisable pour Axon Server
            ErrorResponse errorDetails = new ErrorResponse(
                java.time.Instant.now().toString(),
                404,
                "Not Found",
                ex.getMessage(),
                "/queries/accounts/" + query.getAccountId() + "/operations"
            );
            throw new QueryExecutionException(ex.getMessage(), ex, errorDetails);
        }
    }

    /**
     * Convertit une entité Account en AccountDTO pour la sérialisation via Axon Server
     */
    private AccountDTO convertToAccountDTO(Account account) {
        return AccountDTO.builder()
            .id(account.getId())
            .createdAt(account.getCreatedAt())
            .balance(account.getBalance())
            .status(account.getStatus())
            .currency(account.getCurrency())
            .build();
    }

    /**
     * Convertit une entité Operation en OperationDTO pour la sérialisation via Axon Server
     */
    private OperationDTO convertToOperationDTO(Operation operation) {
        return OperationDTO.builder()
            .id(operation.getId())
            .date(operation.getDate())
            .amount(operation.getAmount())
            .type(operation.getType())
            .accountId(operation.getAccount().getId())
            .build();
    }
}
