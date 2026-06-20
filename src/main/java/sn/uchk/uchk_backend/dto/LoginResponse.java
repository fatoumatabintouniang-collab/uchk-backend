package sn.uchk.uchk_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Réponse de connexion contenant le token JWT")
public class LoginResponse {

    @Schema(description = "Token JWT à utiliser dans le header Authorization: Bearer <token>",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Message d'information", example = "Connexion réussie")
    private String message;

    // Constructeur simple pour compatibilité avec le code existant
    public LoginResponse(String token) {
        this.token = token;
        this.message = "Connexion réussie";
    }
}
