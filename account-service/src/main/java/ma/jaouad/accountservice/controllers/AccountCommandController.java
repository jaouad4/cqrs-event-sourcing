package ma.jaouad.accountservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import ma.jaouad.coreapi.commands.CreateAccountCommand;
import ma.jaouad.coreapi.commands.CreditAccountCommand;
import ma.jaouad.coreapi.commands.DebitAccountCommand;
import ma.jaouad.coreapi.commands.UpdateAccountStatusCommand;
import ma.jaouad.coreapi.dtos.CreateAccountRequestDTO;
import ma.jaouad.coreapi.dtos.CreditAccountRequestDTO;
import ma.jaouad.coreapi.dtos.DebitAccountRequestDTO;
import ma.jaouad.coreapi.enums.AccountStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
@AllArgsConstructor
@Tag(name = "Account Commands", description = "API pour les commandes de gestion des comptes (CQRS Write Side)")
public class AccountCommandController {
    
    private final CommandGateway commandGateway;

    @PostMapping("/create")
    @Operation(
        summary = "Créer un nouveau compte bancaire",
        description = "Crée un nouveau compte avec un solde initial et une devise. Le compte est créé avec le statut CREATED."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compte créé avec succès. Retourne l'ID du compte."),
        @ApiResponse(responseCode = "400", description = "Requête invalide (balance négative)")
    })
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO request) {
        return commandGateway.send(new CreateAccountCommand(
            UUID.randomUUID().toString(),
            request.getInitialBalance(),
            request.getCurrency()
        ));
    }

    @PutMapping("/credit")
    @Operation(
        summary = "Créditer un compte",
        description = "Ajoute un montant au solde d'un compte. Le compte doit être ACTIVATED."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compte crédité avec succès"),
        @ApiResponse(responseCode = "400", description = "Compte non activé ou requête invalide"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO request) {
        return commandGateway.send(new CreditAccountCommand(
            request.getAccountId(),
            request.getAmount(),
            request.getCurrency()
        ));
    }

    @PutMapping("/debit")
    @Operation(
        summary = "Débiter un compte",
        description = "Retire un montant du solde d'un compte. Le compte doit être ACTIVATED et avoir un solde suffisant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compte débité avec succès"),
        @ApiResponse(responseCode = "400", description = "Solde insuffisant, compte non activé ou requête invalide"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO request) {
        return commandGateway.send(new DebitAccountCommand(
            request.getAccountId(),
            request.getAmount(),
            request.getCurrency()
        ));
    }

    @PutMapping("/{accountId}/status")
    @Operation(
        summary = "Changer le statut d'un compte",
        description = "Modifie le statut d'un compte (CREATED, ACTIVATED, SUSPENDED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
        @ApiResponse(responseCode = "400", description = "Statut invalide ou déjà dans cet état"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public CompletableFuture<String> updateStatus(
            @Parameter(description = "ID du compte") @PathVariable String accountId,
            @Parameter(description = "Nouveau statut") @RequestParam AccountStatus status) {
        return commandGateway.send(new UpdateAccountStatusCommand(accountId, status));
    }
}
