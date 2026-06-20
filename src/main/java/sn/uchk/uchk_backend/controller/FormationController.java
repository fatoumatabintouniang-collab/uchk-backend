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
import sn.uchk.uchk_backend.entity.Formation;
import sn.uchk.uchk_backend.repository.FormationRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
@Tag(name = "Formations", description = "Gestion des programmes de formation (dates, niveaux, financements, effectifs par genre)")
@SecurityRequirement(name = "bearerAuth")
public class FormationController {

    private final FormationRepository repo;

    @Operation(summary = "Lister toutes les formations")
    @GetMapping
    public List<Formation> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Lister les formations actives")
    @GetMapping("/actives")
    public List<Formation> getActives() {
        return repo.findByActive(true);
    }

    @Operation(summary = "Filtrer par type de formation",
        description = "Types : LICENCE, MASTER, DUT, BTS, FORMATION_CONTINUE, CERTIFICATION")
    @GetMapping("/type/{type}")
    public List<Formation> getByType(
            @Parameter(description = "Type de formation", example = "MASTER")
            @PathVariable Formation.TypeFormation type) {
        return repo.findByType(type);
    }

    @Operation(summary = "Filtrer par niveau",
        description = "Exemples : BAC+3, BAC+5, BAC+2")
    @GetMapping("/niveau/{niveau}")
    public List<Formation> getByNiveau(
            @Parameter(description = "Niveau de la formation", example = "BAC+5")
            @PathVariable String niveau) {
        return repo.findByNiveau(niveau);
    }

    @Operation(summary = "Trouver une formation par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Formation trouvée"),
        @ApiResponse(responseCode = "404", description = "Formation introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID de la formation", example = "1") @PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Formation introuvable avec l'id : " + id)));
    }

    @Operation(
        summary = "Créer une formation",
        description = """
            Enregistre une nouvelle formation.
            
            - **type** : `LICENCE`, `MASTER`, `DUT`, `BTS`, `FORMATION_CONTINUE`, `CERTIFICATION`
            - **typeFinancement** : `PUBLIC`, `PRIVE`, `CERTIFICATION`, `PARTENARIAT`
            - **nombreFormesHomme / nombreFormesFemme** : effectifs ventilés par genre (obligatoire)
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = @ExampleObject(value = """
            {
              "intitule": "Master Ingénierie Logicielle",
              "type": "MASTER",
              "niveau": "BAC+5",
              "dateDebut": "2025-10-01",
              "dateFin": "2027-06-30",
              "montantFinancement": 0,
              "typeFinancement": "PUBLIC",
              "nombreFormesHomme": 30,
              "nombreFormesFemme": 20,
              "description": "Formation de haut niveau en développement logiciel et architecture système.",
              "active": true
            }
        """))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Formation créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<?> create(@RequestBody Formation formation) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(formation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Modifier une formation")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Formation data) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(f -> {
                    f.setIntitule(data.getIntitule());
                    f.setType(data.getType());
                    f.setNiveau(data.getNiveau());
                    f.setDateDebut(data.getDateDebut());
                    f.setDateFin(data.getDateFin());
                    f.setMontantFinancement(data.getMontantFinancement());
                    f.setTypeFinancement(data.getTypeFinancement());
                    f.setNombreFormesHomme(data.getNombreFormesHomme());
                    f.setNombreFormesFemme(data.getNombreFormesFemme());
                    f.setDescription(data.getDescription());
                    f.setActive(data.getActive());
                    return ResponseEntity.ok(repo.save(f));
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Formation introuvable avec l'id : " + id)));
    }

    @Operation(summary = "Supprimer une formation", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Formation introuvable avec l'id : " + id));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}