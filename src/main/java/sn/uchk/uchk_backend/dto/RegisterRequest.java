package sn.uchk.uchk_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Données nécessaires pour créer un nouveau compte utilisateur")
public class RegisterRequest {

    @Schema(description = "Nom de famille", example = "Diallo")
    private String nom;

    @Schema(description = "Prénom", example = "Fatou")
    private String prenom;

    @Schema(
        description = "Adresse email (sera utilisée pour la connexion). Format attendu : prenom.nom@uchk.edu.sn ou adresse personnelle",
        example = "fatou.diallo@uchk.edu.sn"
    )
    private String email;

    @Schema(
        description = "INE de l'étudiant (Identifiant National Étudiant). Peut aussi être utilisé pour se connecter. Laisser vide pour les non-étudiants.",
        example = "INE2026001"
    )
    private String ine;

    @Schema(description = "Mot de passe (minimum 6 caractères)", example = "motdepasse123")
    private String password;

    @Schema(
        description = "Rôle attribué. Valeurs : ADMIN, ADMINISTRATIF, ENSEIGNANT, ENSEIGNANT_ASSOCIE, RESPONSABLE_FORMATION, TUTEUR, APPUI_INSERTION, ETUDIANT",
        example = "ETUDIANT"
    )
    private String roleNom;
}