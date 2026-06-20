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
import sn.uchk.uchk_backend.dto.EtudiantDTO;
import sn.uchk.uchk_backend.service.EtudiantService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
@Tag(name = "Étudiants", description = "Gestion des dossiers étudiants (Module Étudiant)")
@SecurityRequirement(name = "bearerAuth")
public class EtudiantController {

    private final EtudiantService etudiantService;

    @Operation(
        summary = "Lister tous les étudiants",
        description = "Retourne la liste complète des étudiants. Accessible aux rôles ADMIN, ADMINISTRATIF, ENSEIGNANT, APPUI_INSERTION."
    )
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'RESPONSABLE_FORMATION', 'APPUI_INSERTION')")
    public ResponseEntity<List<EtudiantDTO>> getAll() {
        return ResponseEntity.ok(etudiantService.findAll());
    }

    @Operation(
        summary = "Trouver un étudiant par ID",
        description = "Retourne les informations d'un étudiant. Un étudiant peut consulter son propre dossier."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable",
            content = @Content(examples = @ExampleObject(value = "{\"error\": \"Étudiant introuvable avec l'id : 5\"}")))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'RESPONSABLE_FORMATION', 'APPUI_INSERTION', 'ETUDIANT')")
    public ResponseEntity<?> getById(
            @Parameter(description = "Identifiant interne de l'étudiant", example = "1") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(etudiantService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
        summary = "Trouver un étudiant par INE",
        description = "Recherche un étudiant à partir de son numéro INE."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
        @ApiResponse(responseCode = "404", description = "Aucun étudiant avec cet INE")
    })
    @GetMapping("/ine/{ine}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'APPUI_INSERTION', 'ETUDIANT')")
    public ResponseEntity<?> getByIne(
            @Parameter(description = "Numéro INE de l'étudiant", example = "SN2024001") @PathVariable String ine) {
        try {
            return ResponseEntity.ok(etudiantService.findByIne(ine));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
        summary = "Lister les étudiants d'une formation",
        description = "Retourne tous les étudiants inscrits dans une formation donnée."
    )
    @GetMapping("/formation/{formationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'RESPONSABLE_FORMATION', 'APPUI_INSERTION')")
    public ResponseEntity<List<EtudiantDTO>> getByFormation(
            @Parameter(description = "ID de la formation", example = "1") @PathVariable Long formationId) {
        return ResponseEntity.ok(etudiantService.findByFormation(formationId));
    }

    @Operation(
        summary = "Créer un nouvel étudiant",
        description = "Enregistre un nouveau dossier étudiant. Réservé à ADMIN et ADMINISTRATIF."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données de l'étudiant à créer",
        required = true,
        content = @Content(examples = @ExampleObject(value = """
            {
              "ine": "SN2024001",
              "nom": "Diallo",
              "prenom": "Fatou",
              "dateNaissance": "2000-05-15",
              "promo": "2024",
              "anneeDebut": 2024,
              "anneeSortie": null,
              "telephone": "+221 77 000 0000",
              "email": "fatou.diallo@uchk.sn",
              "adresse": "Dakar, Sénégal",
              "formationId": 1
            }
        """))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> create(@RequestBody EtudiantDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(etudiantService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
        summary = "Modifier un étudiant",
        description = "Met à jour les informations d'un étudiant existant."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant mis à jour"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> update(
            @Parameter(description = "ID de l'étudiant à modifier") @PathVariable Long id,
            @RequestBody EtudiantDTO dto) {
        try {
            return ResponseEntity.ok(etudiantService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
        summary = "Supprimer un étudiant",
        description = "Supprime définitivement le dossier d'un étudiant. Réservé à l'ADMIN."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Étudiant supprimé"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID de l'étudiant à supprimer") @PathVariable Long id) {
        try {
            etudiantService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
