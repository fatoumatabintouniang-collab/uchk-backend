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
import sn.uchk.uchk_backend.dto.StatistiquesInsertionDTO;
import sn.uchk.uchk_backend.entity.InsertionProfessionnelle;
import sn.uchk.uchk_backend.repository.InsertionProfessionnelleRepository;

import java.util.List;

@RestController
@RequestMapping("/api/insertions")
@RequiredArgsConstructor
@Tag(name = "Appui à l'Insertion – Insertion Professionnelle", description = "Suivi de l'insertion professionnelle des diplômés : emploi salarié, auto-emploi, poursuite d'études")
@SecurityRequirement(name = "bearerAuth")
public class InsertionProfessionnelleController {

    private final InsertionProfessionnelleRepository repo;

    @Operation(summary = "Lister toutes les insertions professionnelles")
    @GetMapping
    public List<InsertionProfessionnelle> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Insertions d'un étudiant",
        description = "Retourne le parcours d'insertion professionnelle d'un étudiant donné.")
    @GetMapping("/etudiant/{id}")
    public List<InsertionProfessionnelle> getByEtudiant(
            @Parameter(description = "ID de l'étudiant", example = "1") @PathVariable Long id) {
        return repo.findByEtudiantId(id);
    }

    @Operation(summary = "Statistiques d'insertion",
        description = "Retourne un tableau de bord statistique : nombre de sortants par type d'insertion (emploi salarié, auto-emploi, poursuite d'études, sans emploi) et total général.")
    @ApiResponse(responseCode = "200", description = "Statistiques calculées",
        content = @Content(examples = @ExampleObject(value = """
            {
              "emploiSalarie": 45,
              "autoEmploi": 12,
              "poursuiteEtudes": 8,
              "sansEmploi": 5,
              "total": 70
            }
        """)))
    @GetMapping("/statistiques")
    public StatistiquesInsertionDTO getStatistiques() {
        long emploi = repo.countByType(InsertionProfessionnelle.TypeInsertion.EMPLOI_SALARIE);
        long auto = repo.countByType(InsertionProfessionnelle.TypeInsertion.AUTO_EMPLOI);
        long etudes = repo.countByType(InsertionProfessionnelle.TypeInsertion.POURSUITE_ETUDES);
        long sans = repo.countByType(InsertionProfessionnelle.TypeInsertion.SANS_EMPLOI);
        return new StatistiquesInsertionDTO(emploi, auto, etudes, sans, emploi + auto + etudes + sans);
    }

    @Operation(summary = "Trouver une insertion par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Insertion trouvée"),
        @ApiResponse(responseCode = "404", description = "Insertion introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InsertionProfessionnelle> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Enregistrer une insertion professionnelle",
        description = "Crée un nouveau suivi d'insertion pour un diplômé. Types : EMPLOI_SALARIE, AUTO_EMPLOI, POURSUITE_ETUDES, SANS_EMPLOI.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données de l'insertion",
        content = @Content(examples = @ExampleObject(value = """
            {
              "type": "EMPLOI_SALARIE",
              "poste": "Développeur Full Stack",
              "employeur": "Orange Sénégal",
              "datePrisePoste": "2026-03-01",
              "secteurActivite": "Télécommunications",
              "telephone": "+221 77 999 8888",
              "email": "contact@orange.sn",
              "notes": "CDI, salaire compétitif",
              "etudiant": { "id": 1 }
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'APPUI_INSERTION')")
    public ResponseEntity<InsertionProfessionnelle> create(@RequestBody InsertionProfessionnelle insertion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(insertion));
    }

    @Operation(summary = "Modifier une insertion professionnelle")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPUI_INSERTION')")
    public ResponseEntity<InsertionProfessionnelle> update(@PathVariable Long id, @RequestBody InsertionProfessionnelle data) {
        return repo.findById(id).map(i -> {
            i.setType(data.getType());
            i.setPoste(data.getPoste());
            i.setEmployeur(data.getEmployeur());
            i.setDatePrisePoste(data.getDatePrisePoste());
            i.setSecteurActivite(data.getSecteurActivite());
            i.setTelephone(data.getTelephone());
            i.setEmail(data.getEmail());
            i.setNotes(data.getNotes());
            return ResponseEntity.ok(repo.save(i));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une insertion professionnelle", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}