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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.entity.AutreFormation;
import sn.uchk.uchk_backend.repository.AutreFormationRepository;

import java.util.List;

@RestController
@RequestMapping("/api/autres-formations")
@RequiredArgsConstructor
@Tag(name = "Étudiants – Autres Formations", description = "Gestion des formations complémentaires suivies par les étudiants (certifications, MOOCs, formations professionnelles...)")
@SecurityRequirement(name = "bearerAuth")
public class AutreFormationController {

    private final AutreFormationRepository repo;

    @Operation(summary = "Autres formations d'un étudiant",
        description = "Retourne toutes les formations complémentaires d'un étudiant identifié par son ID.")
    @GetMapping("/etudiant/{id}")
    public List<AutreFormation> getByEtudiant(
            @Parameter(description = "ID de l'étudiant", example = "1") @PathVariable Long id) {
        return repo.findByEtudiantId(id);
    }

    @Operation(summary = "Ajouter une autre formation à un étudiant",
        description = "Enregistre une formation complémentaire (certification, MOOC, formation professionnelle, etc.) pour un étudiant.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données de la formation complémentaire",
        content = @Content(examples = @ExampleObject(value = """
            {
              "intitule": "AWS Cloud Practitioner",
              "etablissement": "Amazon Web Services",
              "dateDebut": "2025-09-01",
              "dateFin": "2025-11-30",
              "type": "CERTIFICATION",
              "certificat": "https://aws.amazon.com/certificate/xxx",
              "etudiant": { "id": 1 }
            }
        """))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Formation complémentaire ajoutée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ETUDIANT')")
    public ResponseEntity<AutreFormation> create(@RequestBody AutreFormation af) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(af));
    }

    @Operation(summary = "Supprimer une autre formation", description = "Réservé aux rôles ADMIN et ADMINISTRATIF.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la formation complémentaire") @PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}