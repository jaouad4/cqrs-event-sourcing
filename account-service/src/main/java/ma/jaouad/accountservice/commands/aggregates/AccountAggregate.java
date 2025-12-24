package ma.jaouad.accountservice.commands.aggregates;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.jaouad.coreapi.commands.CreateAccountCommand;
import ma.jaouad.coreapi.commands.CreditAccountCommand;
import ma.jaouad.coreapi.commands.DebitAccountCommand;
import ma.jaouad.coreapi.commands.UpdateAccountStatusCommand;
import ma.jaouad.coreapi.enums.AccountStatus;
import ma.jaouad.coreapi.events.AccountCreatedEvent;
import ma.jaouad.coreapi.events.AccountCreditedEvent;
import ma.jaouad.coreapi.events.AccountDebitedEvent;
import ma.jaouad.coreapi.events.AccountStatusUpdatedEvent;
import ma.jaouad.coreapi.exceptions.InsufficientBalanceException;
import ma.jaouad.coreapi.exceptions.InvalidAccountStatusException;
import ma.jaouad.coreapi.exceptions.NegativeBalanceException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
@Getter
@Setter
public class AccountAggregate {
    
    @AggregateIdentifier
    private String accountId;
    private double currentBalance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        log.info("Account Aggregate Created");
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        log.info("CreateAccount Command Received");
        if (command.getInitialBalance() < 0) {
            throw new NegativeBalanceException(command.getInitialBalance());
        }
        AggregateLifecycle.apply(new AccountCreatedEvent(
            command.getId(),
            command.getInitialBalance(),
            command.getCurrency(),
            AccountStatus.CREATED
        ));
    }

    @CommandHandler
    public void handleCommand(CreditAccountCommand command) {
        log.info("CreditAccountCommand Received");
        if (!this.status.equals(AccountStatus.ACTIVATED)) {
            throw new InvalidAccountStatusException(command.getId(), this.status, "CREDIT");
        }
        AggregateLifecycle.apply(new AccountCreditedEvent(
            command.getId(),
            command.getAmount()
        ));
    }

    @CommandHandler
    public void handleCommand(DebitAccountCommand command) {
        log.info("DebitAccountCommand Received");
        if (!this.status.equals(AccountStatus.ACTIVATED)) {
            throw new InvalidAccountStatusException(command.getId(), this.status, "DEBIT");
        }
        if (command.getAmount() > currentBalance) {
            throw new InsufficientBalanceException(command.getId(), currentBalance, command.getAmount());
        }
        AggregateLifecycle.apply(new AccountDebitedEvent(
            command.getId(),
            command.getAmount()
        ));
    }

    @CommandHandler
    public void handleCommand(UpdateAccountStatusCommand command) {
        log.info("UpdateAccountStatusCommand Received");
        if (this.status.equals(command.getAccountStatus())) {
            throw new RuntimeException("Account is already in " + status + " state");
        }
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(
            command.getId(),
            this.status,
            command.getAccountStatus()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("AccountCreatedEvent occurred");
        this.accountId = event.getAccountId();
        this.currentBalance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = event.getAccountStatus();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        log.info("AccountCreditedEvent occurred");
        this.currentBalance += event.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        log.info("AccountDebitedEvent occurred");
        this.currentBalance -= event.getAmount();
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("AccountStatusUpdatedEvent occurred");
        this.status = event.getToStatus();
    }
}
