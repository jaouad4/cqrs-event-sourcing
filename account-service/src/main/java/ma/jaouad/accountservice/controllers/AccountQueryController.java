package ma.jaouad.accountservice.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import ma.jaouad.accountservice.queries.dto.AccountDTO;
import ma.jaouad.accountservice.queries.dto.AccountListResponse;
import ma.jaouad.accountservice.queries.dto.OperationDTO;
import ma.jaouad.accountservice.queries.dto.OperationListResponse;
import ma.jaouad.coreapi.queries.GetAccountByIdQuery;
import ma.jaouad.coreapi.queries.GetAccountOperationsQuery;
import ma.jaouad.coreapi.queries.GetAllAccountsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/queries/accounts")
@AllArgsConstructor
@Tag(name = "Account Queries", description = "API pour les requêtes de consultation des comptes (CQRS Read Side)")
public class AccountQueryController {
    
    private final QueryGateway queryGateway;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Lister tous les comptes",
        description = "Récupère la liste de tous les comptes bancaires avec leurs informations"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes récupérée avec succès")
    })
    public CompletableFuture<List<AccountDTO>> getAllAccounts() {
        return queryGateway.query(
            new GetAllAccountsQuery(),
            ResponseTypes.instanceOf(AccountListResponse.class)
        ).thenApply(AccountListResponse::getAccounts);
    }

    @GetMapping("/{accountId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Consulter un compte par ID",
        description = "Récupère les détails d'un compte spécifique par son identifiant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compte trouvé"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public CompletableFuture<AccountDTO> getAccountById(
            @Parameter(description = "ID du compte") @PathVariable String accountId) {
        return queryGateway.query(
            new GetAccountByIdQuery(accountId),
            ResponseTypes.instanceOf(AccountDTO.class)
        );
    }

    @GetMapping("/{accountId}/operations")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Consulter l'historique des opérations",
        description = "Récupère toutes les opérations (crédits et débits) effectuées sur un compte"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historique des opérations récupéré"),
        @ApiResponse(responseCode = "404", description = "Compte non trouvé")
    })
    public CompletableFuture<List<OperationDTO>> getAccountOperations(
            @Parameter(description = "ID du compte") @PathVariable String accountId) {
        return queryGateway.query(
            new GetAccountOperationsQuery(accountId),
            ResponseTypes.instanceOf(OperationListResponse.class)
        ).thenApply(OperationListResponse::getOperations);
    }
}
