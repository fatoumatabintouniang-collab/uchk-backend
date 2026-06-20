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
import sn.uchk.uchk_backend.entity.Reunion;
import sn.uchk.uchk_backend.repository.ReunionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/reunions")
@RequiredArgsConstructor
@Tag(name = "Formations – Réunions", description = "Gestion des réunions pédagogiques : suivi tutorat, préparation des cours et des évaluations")
@SecurityRequirement(name = "bearerAuth")
public class ReunionController {

    private final ReunionRepository repo;

    @Operation(summary = "Lister toutes les réunions")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Reunion> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Réunions d'une formation",
        description = "Retourne toutes les réunions associées à une formation donnée.")
    @GetMapping("/formation/{id}")
    public List<Reunion> getByFormation(
            @Parameter(description = "ID de la formation", example = "1") @PathVariable Long id) {
        return repo.findByFormationId(id);
    }

    @Operation(summary = "Filtrer par type de réunion",
        description = "Types disponibles : TUTORAT, PREPARATION_COURS, PREPARATION_EVALUATION, AUTRE")
    @GetMapping("/type/{type}")
    public List<Reunion> getByType(
            @Parameter(description = "Type de réunion pédagogique", example = "PREPARATION_COURS") @PathVariable Reunion.TypeReunion type) {
        return repo.findByTypeReunion(type);
    }

    @Operation(summary = "Trouver une réunion par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Réunion trouvée"),
        @ApiResponse(responseCode = "404", description = "Réunion introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reunion> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer une réunion pédagogique")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données de la réunion",
        content = @Content(examples = @ExampleObject(value = """
            {
              "titre": "Réunion de préparation – Examen final M2",
              "dateHeure": "2026-05-25T10:00:00",
              "lieu": "Salle B202",
              "typeReunion": "PREPARATION_EVALUATION",
              "participants": "Tous les enseignants du Master IL",
              "ordreJour": "1. Modalités d'examen\\n2. Sujets proposés\\n3. Planning de correction",
              "compteRendu": null,
              "effectuee": false,
              "formation": { "id": 1 }
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public ResponseEntity<Reunion> create(@RequestBody Reunion reunion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(reunion));
    }

    @Operation(summary = "Modifier une réunion")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<Reunion> update(@PathVariable Long id, @RequestBody Reunion data) {
        return repo.findById(id).map(r -> {
            r.setTitre(data.getTitre());
            r.setDateHeure(data.getDateHeure());
            r.setLieu(data.getLieu());
            r.setTypeReunion(data.getTypeReunion());
            r.setParticipants(data.getParticipants());
            r.setOrdreJour(data.getOrdreJour());
            r.setCompteRendu(data.getCompteRendu());
            r.setEffectuee(data.getEffectuee());
            r.setFormation(data.getFormation());
            return ResponseEntity.ok(repo.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une réunion", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}