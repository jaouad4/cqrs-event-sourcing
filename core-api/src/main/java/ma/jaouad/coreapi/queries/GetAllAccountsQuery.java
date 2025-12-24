package ma.jaouad.coreapi.queries;

import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Query pour récupérer tous les comptes
 * 
 * Query sans paramètres - le constructeur par défaut est suffisant
 * pour la désérialisation Jackson
 */
@NoArgsConstructor
public class GetAllAccountsQuery implements Serializable {
    // Query sans paramètres - juste l'implémentation de Serializable pour Axon Server
}
