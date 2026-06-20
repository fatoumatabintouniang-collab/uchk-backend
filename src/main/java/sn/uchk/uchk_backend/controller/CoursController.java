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
import sn.uchk.uchk_backend.dto.CoursDTO;
import sn.uchk.uchk_backend.service.CoursService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "Gestion des cours / matières rattachés à une formation (Formation → Cours → EmploiDuTemps)")
@SecurityRequirement(name = "bearerAuth")
public class CoursController {

    private final CoursService coursService;

    // ── GET tous ─────────────────────────────────────────────────────────────

    @Operation(summary = "Lister tous les cours")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<CoursDTO> getAll() {
        return coursService.getAll();
    }

    // ── GET par formation ─────────────────────────────────────────────────────

    @Operation(summary = "Cours d'une formation",
        description = "Retourne tous les cours rattachés à une formation donnée.")
    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<CoursDTO> getByFormation(
            @Parameter(description = "ID de la formation", example = "1")
            @PathVariable Long formationId) {
        return coursService.getByFormation(formationId);
    }

    // ── GET par formateur ─────────────────────────────────────────────────────

    @Operation(summary = "Cours assignés à un formateur")
    @GetMapping("/formateur/{formateurId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<CoursDTO> getByFormateur(
            @Parameter(description = "ID du formateur", example = "2")
            @PathVariable Long formateurId) {
        return coursService.getByFormateur(formateurId);
    }

    // ── GET par ID ────────────────────────────────────────────────────────────

    @Operation(summary = "Trouver un cours par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cours trouvé"),
        @ApiResponse(responseCode = "404", description = "Cours introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public ResponseEntity<CoursDTO> getById(
            @Parameter(description = "ID du cours", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(coursService.getById(id));
    }

    // ── POST créer ────────────────────────────────────────────────────────────

    @Operation(
        summary = "Créer un cours",
        description = """
            Crée un nouveau cours rattaché à une formation.
            
            - **formationId** : obligatoire
            - **formateurId** : optionnel (formateur responsable)
            - **semestre** : S1, S2, ANNUEL, etc.
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = @ExampleObject(value = """
            {
              "intitule": "Algorithmique Avancée",
              "code": "INFO301",
              "description": "Structures de données et algorithmes complexes",
              "volumeHoraire": 45,
              "coefficient": 3.0,
              "semestre": "S1",
              "actif": true,
              "formationId": 1,
              "formateurId": 2
            }
        """))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cours créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Code cours déjà utilisé")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<?> create(@RequestBody CoursDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(coursService.create(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT modifier ──────────────────────────────────────────────────────────

    @Operation(summary = "Modifier un cours")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CoursDTO dto) {
        try {
            return ResponseEntity.ok(coursService.update(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE supprimer ──────────────────────────────────────────────────────

    @Operation(summary = "Supprimer un cours", description = "Réservé à l'ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cours supprimé"),
        @ApiResponse(responseCode = "404", description = "Cours introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            coursService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
