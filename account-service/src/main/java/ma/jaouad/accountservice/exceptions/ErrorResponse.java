package ma.jaouad.accountservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response d'erreur standardisee pour l'API REST
 * 
 * N'utilise pas Instant pour eviter les problemes de serialisation
 * Utilise String pour le timestamp (format ISO-8601)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
