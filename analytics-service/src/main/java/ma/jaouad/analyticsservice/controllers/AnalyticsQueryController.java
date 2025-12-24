package ma.jaouad.analyticsservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import ma.jaouad.analyticsservice.queries.entities.AccountView;
import ma.jaouad.analyticsservice.queries.repositories.AccountViewRepository;
import ma.jaouad.analyticsservice.queries.repositories.OperationViewRepository;
import ma.jaouad.coreapi.enums.OperationType;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@AllArgsConstructor
@Tag(name = "Analytics", description = "API pour les statistiques et analyses des comptes et opérations")
public class AnalyticsQueryController {
    
    private final AccountViewRepository accountViewRepository;
    private final OperationViewRepository operationViewRepository;

    @GetMapping("/accounts")
    @Operation(
        summary = "Lister tous les comptes (vue analytics)",
        description = "Récupère tous les comptes avec des informations analytiques (nombre d'opérations, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes analytics récupérée avec succès")
    })
    public List<AccountView> getAllAccounts() {
        return accountViewRepository.findAll();
    }

    @GetMapping("/accounts/total")
    @Operation(
        summary = "Obtenir les totaux des comptes",
        description = "Retourne le nombre total de comptes et le solde global de tous les comptes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Totaux calculés avec succès")
    })
    public Map<String, Object> getTotalAccounts() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalAccounts", accountViewRepository.getTotalAccounts());
        result.put("totalBalance", accountViewRepository.getTotalBalance());
        return result;
    }

    @GetMapping("/operations/statistics")
    @Operation(
        summary = "Obtenir les statistiques des opérations",
        description = "Retourne des statistiques détaillées : nombre de crédits/débits et montants totaux"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès")
    })
    public Map<String, Object> getOperationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCredits", operationViewRepository.countByType(OperationType.CREDIT));
        stats.put("totalDebits", operationViewRepository.countByType(OperationType.DEBIT));
        stats.put("amountCredits", operationViewRepository.sumAmountByType(OperationType.CREDIT));
        stats.put("amountDebits", operationViewRepository.sumAmountByType(OperationType.DEBIT));
        return stats;
    }
}
