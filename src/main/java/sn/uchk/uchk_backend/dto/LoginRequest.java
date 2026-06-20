package sn.uchk.uchk_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Identifiants de connexion")
public class LoginRequest {

    @Schema(
        description = "Email universitaire ou INE de l'étudiant. Exemples : jean.dupont@uchk.edu.sn, INE2026001",
        example = "jean.dupont@uchk.edu.sn"
    )
    private String identifiant;

    @Schema(description = "Mot de passe du compte", example = "motdepasse123")
    private String password;
}