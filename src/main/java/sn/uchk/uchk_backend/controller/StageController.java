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
import sn.uchk.uchk_backend.entity.Stage;
import sn.uchk.uchk_backend.repository.StageRepository;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
@Tag(name = "Appui à l'Insertion – Stages", description = "Suivi des stages des étudiants : entreprise, durée, tuteur, statut et rapport")
@SecurityRequirement(name = "bearerAuth")
public class StageController {

    private final StageRepository repo;

    @Operation(summary = "Lister tous les stages")
    @GetMapping
    public List<Stage> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Stages d'un étudiant",
        description = "Retourne tous les stages d'un étudiant identifié par son ID.")
    @GetMapping("/etudiant/{id}")
    public List<Stage> getByEtudiant(
            @Parameter(description = "ID de l'étudiant", example = "1") @PathVariable Long id) {
        return repo.findByEtudiantId(id);
    }

    @Operation(summary = "Stages suivis par un tuteur",
        description = "Retourne tous les stages pour lesquels un formateur/tuteur est responsable de suivi.")
    @GetMapping("/tuteur/{id}")
    public List<Stage> getByTuteur(
            @Parameter(description = "ID du tuteur (formateur)", example = "3") @PathVariable Long id) {
        return repo.findByTuteurId(id);
    }

    @Operation(summary = "Filtrer les stages par statut",
        description = "Statuts disponibles : EN_COURS, TERMINE, VALIDE, REFUSE")
    @GetMapping("/statut/{statut}")
    public List<Stage> getByStatut(
            @Parameter(description = "Statut du stage", example = "EN_COURS") @PathVariable Stage.StatutStage statut) {
        return repo.findByStatut(statut);
    }

    @Operation(summary = "Trouver un stage par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stage trouvé"),
        @ApiResponse(responseCode = "404", description = "Stage introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Stage> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un stage",
        description = "Enregistre un nouveau stage. L'étudiant et le tuteur doivent exister dans la base. Le tuteur est un formateur de type TUTEUR.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du stage",
        content = @Content(examples = @ExampleObject(value = """
            {
              "titre": "Stage de fin d'études – Développement Web",
              "entreprise": "Sonatel",
              "adresseEntreprise": "Dakar, Sénégal",
              "dateDebut": "2026-02-01",
              "dateFin": "2026-07-31",
              "statut": "EN_COURS",
              "rapport": null,
              "note": null,
              "commentaire": "Stage bien avancé",
              "etudiant": { "id": 1 },
              "tuteur": { "id": 2 }
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'APPUI_INSERTION')")
    public ResponseEntity<Stage> create(@RequestBody Stage stage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(stage));
    }

    @Operation(summary = "Modifier un stage")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'APPUI_INSERTION')")
    public ResponseEntity<Stage> update(@PathVariable Long id, @RequestBody Stage data) {
        return repo.findById(id).map(s -> {
            s.setTitre(data.getTitre());
            s.setEntreprise(data.getEntreprise());
            s.setAdresseEntreprise(data.getAdresseEntreprise());
            s.setDateDebut(data.getDateDebut());
            s.setDateFin(data.getDateFin());
            s.setStatut(data.getStatut());
            s.setRapport(data.getRapport());
            s.setNote(data.getNote());
            s.setCommentaire(data.getCommentaire());
            s.setTuteur(data.getTuteur());
            return ResponseEntity.ok(repo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un stage", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}