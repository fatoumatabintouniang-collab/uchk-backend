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
import sn.uchk.uchk_backend.entity.Budget;
import sn.uchk.uchk_backend.repository.BudgetRepository;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Administration – Budgets", description = "Gestion budgétaire : projets de budget prévisionnels et budgets réalisés")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetRepository repo;

    @Operation(summary = "Lister tous les budgets")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Budget> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Filtrer les budgets par année",
        description = "Retourne tous les budgets (prévisionnels et réalisés) pour une année donnée.")
    @GetMapping("/annee/{annee}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Budget> getByAnnee(
            @Parameter(description = "Année du budget", example = "2026") @PathVariable Integer annee) {
        return repo.findByAnnee(annee);
    }

    @Operation(summary = "Filtrer les budgets par type",
        description = "Type PREVISIONNEL : projet de budget en cours. Type REALISE : budget exécuté.")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Budget> getByType(
            @Parameter(description = "Type de budget : PREVISIONNEL ou REALISE", example = "PREVISIONNEL") @PathVariable Budget.TypeBudget type) {
        return repo.findByType(type);
    }

    @Operation(summary = "Trouver un budget par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Budget trouvé"),
        @ApiResponse(responseCode = "404", description = "Budget introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Budget> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un budget",
        description = "Enregistre un nouveau budget prévisionnel ou réalisé.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du budget",
        content = @Content(examples = @ExampleObject(value = """
            {
              "intitule": "Budget de fonctionnement 2026",
              "annee": 2026,
              "type": "PREVISIONNEL",
              "montantPrevisionnel": 50000000.00,
              "montantRealise": 0.00,
              "categorie": "FONCTIONNEMENT",
              "notes": "Budget approuvé en conseil d'université",
              "dateCreation": "2026-01-15"
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Budget> create(@RequestBody Budget budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(budget));
    }

    @Operation(summary = "Modifier un budget")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Budget> update(@PathVariable Long id, @RequestBody Budget data) {
        return repo.findById(id).map(b -> {
            b.setIntitule(data.getIntitule());
            b.setAnnee(data.getAnnee());
            b.setType(data.getType());
            b.setMontantPrevisionnel(data.getMontantPrevisionnel());
            b.setMontantRealise(data.getMontantRealise());
            b.setCategorie(data.getCategorie());
            b.setNotes(data.getNotes());
            b.setDateCreation(data.getDateCreation());
            return ResponseEntity.ok(repo.save(b));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un budget", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}