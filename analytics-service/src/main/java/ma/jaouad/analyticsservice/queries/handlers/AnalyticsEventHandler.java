package ma.jaouad.analyticsservice.queries.handlers;

import lombok.extern.slf4j.Slf4j;
import ma.jaouad.analyticsservice.queries.entities.AccountView;
import ma.jaouad.analyticsservice.queries.entities.OperationView;
import ma.jaouad.analyticsservice.queries.repositories.AccountViewRepository;
import ma.jaouad.analyticsservice.queries.repositories.OperationViewRepository;
import ma.jaouad.coreapi.enums.OperationType;
import ma.jaouad.coreapi.events.AccountCreatedEvent;
import ma.jaouad.coreapi.events.AccountCreditedEvent;
import ma.jaouad.coreapi.events.AccountDebitedEvent;
import ma.jaouad.coreapi.events.AccountStatusUpdatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalyticsEventHandler {
    
    private final AccountViewRepository accountViewRepository;
    private final OperationViewRepository operationViewRepository;

    public AnalyticsEventHandler(AccountViewRepository accountViewRepository,
                                 OperationViewRepository operationViewRepository) {
        this.accountViewRepository = accountViewRepository;
        this.operationViewRepository = operationViewRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<?> eventMessage) {
        log.info("Analytics: AccountCreatedEvent received");
        AccountView accountView = AccountView.builder()
            .id(event.getAccountId())
            .balance(event.getInitialBalance())
            .currency(event.getCurrency())
            .status(event.getAccountStatus())
            .createdAt(eventMessage.getTimestamp())
            .operationCount(0)
            .build();
        accountViewRepository.save(accountView);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, EventMessage<?> eventMessage) {
        log.info("Analytics: AccountCreditedEvent received");
        AccountView accountView = accountViewRepository.findById(event.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        OperationView operation = OperationView.builder()
            .accountId(event.getAccountId())
            .date(eventMessage.getTimestamp())
            .amount(event.getAmount())
            .type(OperationType.CREDIT)
            .build();
        operationViewRepository.save(operation);
        
        accountView.setBalance(accountView.getBalance() + event.getAmount());
        accountView.setOperationCount(accountView.getOperationCount() + 1);
        accountViewRepository.save(accountView);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, EventMessage<?> eventMessage) {
        log.info("Analytics: AccountDebitedEvent received");
        AccountView accountView = accountViewRepository.findById(event.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        OperationView operation = OperationView.builder()
            .accountId(event.getAccountId())
            .date(eventMessage.getTimestamp())
            .amount(event.getAmount())
            .type(OperationType.DEBIT)
            .build();
        operationViewRepository.save(operation);
        
        accountView.setBalance(accountView.getBalance() - event.getAmount());
        accountView.setOperationCount(accountView.getOperationCount() + 1);
        accountViewRepository.save(accountView);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event, EventMessage<?> eventMessage) {
        log.info("Analytics: AccountStatusUpdatedEvent received");
        AccountView accountView = accountViewRepository.findById(event.getAccountId())
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        accountView.setStatus(event.getToStatus());
        accountViewRepository.save(accountView);
    }
}
