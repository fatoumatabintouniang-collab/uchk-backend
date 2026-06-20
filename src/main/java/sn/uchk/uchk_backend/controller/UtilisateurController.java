package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.entity.Utilisateur;
import sn.uchk.uchk_backend.repository.UtilisateurRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs (activation, désactivation, suppression)")
@SecurityRequirement(name = "bearerAuth")
public class UtilisateurController {

    private final UtilisateurRepository repo;

    @Operation(
        summary = "Mon profil",
        description = "Retourne les informations du compte connecté. Accessible à tous les utilisateurs authentifiés."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil retourné"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié."));
        }
        String email = authentication.getName();
        return repo.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Utilisateur introuvable.")));
    }

    @Operation(
        summary = "Lister tous les utilisateurs",
        description = "Retourne tous les comptes du système. Réservé à l'ADMIN."
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Utilisateur> getAll() {
        return repo.findAll();
    }

    @Operation(
        summary = "Lister les utilisateurs actifs",
        description = "Retourne uniquement les comptes actifs. Réservé à ADMIN et ADMINISTRATIF."
    )
    @GetMapping("/actifs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Utilisateur> getActifs() {
        return repo.findByActif(true);
    }

    @Operation(
        summary = "Trouver un utilisateur par ID",
        description = "Accessible à ADMIN et ADMINISTRATIF."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID de l'utilisateur", example = "1") @PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Utilisateur introuvable avec l'id : " + id)));
    }

    @Operation(
        summary = "Activer ou désactiver un compte",
        description = "Un compte désactivé ne peut plus se connecter. Réservé à l'ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleActif(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @Parameter(description = "true = activer, false = désactiver", example = "false")
            @RequestParam Boolean actif) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(u -> {
                    u.setActif(actif);
                    return ResponseEntity.ok(repo.save(u));
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Utilisateur introuvable avec l'id : " + id)));
    }

    @Operation(
        summary = "Supprimer un utilisateur",
        description = "Suppression définitive et irréversible. Réservé à l'ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de l'utilisateur à supprimer") @PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur introuvable avec l'id : " + id));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
