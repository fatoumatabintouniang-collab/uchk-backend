package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.entity.CompteRendu;
import sn.uchk.uchk_backend.repository.CompteRenduRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comptes-rendus")
@RequiredArgsConstructor
@Tag(name = "Communication – Comptes Rendus", description = "Gestion des comptes rendus de réunions, séminaires, webinaires et conseils d'université")
@SecurityRequirement(name = "bearerAuth")
public class CompteRenduController {

    private final CompteRenduRepository repo;

    @Operation(summary = "Lister tous les comptes rendus",
        description = "Retourne l'ensemble des comptes rendus archivés, tous types confondus.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<CompteRendu> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Filtrer par type de réunion",
        description = "Retourne les comptes rendus d'un type spécifique. Valeurs possibles : REUNION, RENCONTRE, SEMINAIRE, WEBINAIRE, CONSEIL_UNIVERSITE, AUTRE")
    @GetMapping("/type/{type}")
    public List<CompteRendu> getByType(
            @Parameter(description = "Type de réunion", example = "SEMINAIRE") @PathVariable CompteRendu.TypeReunion type) {
        return repo.findByTypeReunion(type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un compte rendu par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte rendu trouvé"),
        @ApiResponse(responseCode = "404", description = "Compte rendu introuvable")
    })
    public ResponseEntity<CompteRendu> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un compte rendu",
        description = "Enregistre un nouveau compte rendu. La notification sera initialement à false.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du compte rendu",
        content = @Content(examples = @ExampleObject(value = """
            {
              "titre": "Conseil d'université – Mai 2026",
              "typeReunion": "CONSEIL_UNIVERSITE",
              "dateReunion": "2026-05-20",
              "lieu": "Salle A, Bâtiment Administratif",
              "contenu": "Ordre du jour : budget 2026, nouveaux programmes...",
              "fichierJoint": null
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public CompteRendu create(@RequestBody CompteRendu compteRendu) {
        compteRendu.setNotificationEnvoyee(false);
        return repo.save(compteRendu);
    }

    @Operation(summary = "Modifier un compte rendu")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<CompteRendu> update(@PathVariable Long id, @RequestBody CompteRendu data) {
        return repo.findById(id).map(c -> {
            c.setTitre(data.getTitre());
            c.setTypeReunion(data.getTypeReunion());
            c.setDateReunion(data.getDateReunion());
            c.setLieu(data.getLieu());
            c.setContenu(data.getContenu());
            c.setFichierJoint(data.getFichierJoint());
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Marquer la notification comme envoyée",
        description = "Met à jour le statut de notification d'un compte rendu à true, indiquant que les utilisateurs concernés ont été notifiés.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification marquée comme envoyée"),
        @ApiResponse(responseCode = "404", description = "Compte rendu introuvable")
    })
    @PatchMapping("/{id}/notifier")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<CompteRendu> marquerNotifie(
            @Parameter(description = "ID du compte rendu") @PathVariable Long id) {
        return repo.findById(id).map(c -> {
            c.setNotificationEnvoyee(true);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un compte rendu", description = "Suppression définitive. Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}