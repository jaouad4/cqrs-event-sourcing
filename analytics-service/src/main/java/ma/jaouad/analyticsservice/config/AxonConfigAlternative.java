package ma.jaouad.analyticsservice.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration alternative pour Axon Framework - Approche avec Pattern Matcher
 * 
 * Cette configuration utilise une approche plus flexible et maintenable
 * pour gérer les permissions XStream.
 * 
 * À utiliser si la version simple (AxonConfig.java) pose des problèmes
 * ou si on préfère une approche basée sur les patterns.
 */
@Configuration
public class AxonConfigAlternative {

    /**
     * Configure XStream en utilisant un pattern matcher pour les packages autorisés
     * Approche plus flexible que d'énumérer les classes une par une
     * 
     * @param xStream Instance XStream
     * @return XStream configuré
     */
    @ConditionalOnBean(XStream.class)
    @Bean
    public XStream xStreamPatternCustomizer(XStream xStream) {
        // Approche 1: Autoriser tous les packages du domaine
        xStream.allowTypesByWildcard(new String[]{
            "ma.jaouad.coreapi.**",
            "ma.jaouad.accountservice.**",
            "ma.jaouad.analyticsservice.**"
        });

        return xStream;
    }
}
