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
import sn.uchk.uchk_backend.entity.Formateur;
import sn.uchk.uchk_backend.repository.FormateurRepository;

import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@RequiredArgsConstructor
@Tag(name = "Formateurs", description = "Gestion des enseignants, enseignants associés, responsables de formation et tuteurs")
@SecurityRequirement(name = "bearerAuth")
public class FormateurController {

    private final FormateurRepository repo;

    @Operation(summary = "Lister tous les formateurs")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Formateur> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Lister les formateurs actifs",
        description = "Retourne uniquement les formateurs dont le statut 'actif' est true.")
    @GetMapping("/actifs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Formateur> getActifs() {
        return repo.findByActif(true);
    }

    @Operation(summary = "Filtrer par type de formateur",
        description = "Types disponibles : ENSEIGNANT, ENSEIGNANT_ASSOCIE, RESPONSABLE_FORMATION, TUTEUR")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Formateur> getByType(
            @Parameter(description = "Type de formateur", example = "ENSEIGNANT") @PathVariable Formateur.TypeFormateur type) {
        return repo.findByTypeFormateur(type);
    }

    @Operation(summary = "Trouver un formateur par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Formateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Formateur introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public ResponseEntity<Formateur> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un formateur")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du formateur",
        content = @Content(examples = @ExampleObject(value = """
            {
              "nom": "Ndiaye",
              "prenom": "Mamadou",
              "email": "m.ndiaye@uchk.sn",
              "telephone": "+221 77 111 2233",
              "specialite": "Génie Logiciel",
              "typeFormateur": "ENSEIGNANT",
              "grade": "Maître de Conférences",
              "actif": true
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Formateur> create(@RequestBody Formateur formateur) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(formateur));
    }

    @Operation(summary = "Modifier un formateur")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Formateur> update(@PathVariable Long id, @RequestBody Formateur data) {
        return repo.findById(id).map(f -> {
            f.setNom(data.getNom());
            f.setPrenom(data.getPrenom());
            f.setEmail(data.getEmail());
            f.setTelephone(data.getTelephone());
            f.setSpecialite(data.getSpecialite());
            f.setTypeFormateur(data.getTypeFormateur());
            f.setGrade(data.getGrade());
            f.setActif(data.getActif());
            return ResponseEntity.ok(repo.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un formateur", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}