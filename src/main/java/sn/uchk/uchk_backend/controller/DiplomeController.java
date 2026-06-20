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
import sn.uchk.uchk_backend.entity.Diplome;
import sn.uchk.uchk_backend.repository.DiplomeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/diplomes")
@RequiredArgsConstructor
@Tag(name = "Étudiants – Diplômes", description = "Gestion des diplômes obtenus par les étudiants (avant et pendant leur parcours à l'UCHK)")
@SecurityRequirement(name = "bearerAuth")
public class DiplomeController {

    private final DiplomeRepository repo;

    @Operation(summary = "Diplômes d'un étudiant",
        description = "Retourne tous les diplômes d'un étudiant identifié par son ID.")
    @GetMapping("/etudiant/{id}")
    public List<Diplome> getByEtudiant(
            @Parameter(description = "ID de l'étudiant", example = "1") @PathVariable Long id) {
        return repo.findByEtudiantId(id);
    }

    @Operation(summary = "Trouver un diplôme par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diplôme trouvé"),
        @ApiResponse(responseCode = "404", description = "Diplôme introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Diplome> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Ajouter un diplôme à un étudiant")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du diplôme",
        content = @Content(examples = @ExampleObject(value = """
            {
              "intitule": "Licence Informatique",
              "etablissement": "Université Cheikh Anta Diop",
              "dateObtention": "2024-06-30",
              "mention": "Bien",
              "fichier": null,
              "etudiant": { "id": 1 }
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Diplome> create(@RequestBody Diplome diplome) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(diplome));
    }

    @Operation(summary = "Supprimer un diplôme", description = "Réservé aux rôles ADMIN et ADMINISTRATIF.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}