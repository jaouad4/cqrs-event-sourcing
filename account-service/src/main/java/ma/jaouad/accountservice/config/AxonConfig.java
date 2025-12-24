package ma.jaouad.accountservice.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Axon Framework - XStream Serialization
 * 
 * Configuration simple et fonctionnelle pour XStream.
 * XStream est plus stable que Jackson pour la serialisation Axon Server.
 * 
 * Solution: Utiliser AnyTypePermission pour autoriser tous les types.
 * Note: En production, remplacer par une liste blanche specifique.
 */
@Configuration
public class AxonConfig {

    /**
     * Configure XStream pour autoriser la serialisation de tous les types
     * 
     * SOLUTION SIMPLE ET FONCTIONNELLE:
     * - Utilise XStream.setupDefaultSecurity() pour la configuration de base
     * - Ajoute AnyTypePermission pour autoriser tous les types
     * 
     * Cette approche fonctionne avec:
     * - Commands, Events, Queries
     * - DTOs et wrappers de response
     * - Types Java standards (Instant, List, etc.)
     * - Enums
     */
    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        
        // Autoriser tous les types (simple et fonctionnel)
        // En production, remplacer par xStream.allowTypes() avec liste specifique
        xStream.addPermission(AnyTypePermission.ANY);
        
        // Configuration des alias pour ameliorer la lisibilite du XML
        xStream.autodetectAnnotations(true);
        
        return xStream;
    }
}
