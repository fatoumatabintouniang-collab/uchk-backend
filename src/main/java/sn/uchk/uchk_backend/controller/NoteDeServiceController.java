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
import sn.uchk.uchk_backend.entity.NoteDeService;
import sn.uchk.uchk_backend.repository.NoteDeServiceRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notes-service")
@RequiredArgsConstructor
@Tag(name = "Administration – Notes de Service", description = "Gestion des notes de service internes, externes, administratives et circulaires")
@SecurityRequirement(name = "bearerAuth")
public class NoteDeServiceController {

    private final NoteDeServiceRepository repo;

    @Operation(summary = "Lister toutes les notes de service")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<NoteDeService> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Filtrer par type de note",
        description = "Types disponibles : INTERNE (service interne), EXTERNE (partenaires), ADMINISTRATIVE (directions), CIRCULAIRE (niveau central).")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<NoteDeService> getByType(
            @Parameter(description = "Type de note : INTERNE, EXTERNE, ADMINISTRATIVE, CIRCULAIRE") @PathVariable NoteDeService.TypeNote type) {
        return repo.findByType(type);
    }

    @Operation(summary = "Trouver une note par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Note trouvée"),
        @ApiResponse(responseCode = "404", description = "Note introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<NoteDeService> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer une note de service")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données de la note",
        content = @Content(examples = @ExampleObject(value = """
            {
              "reference": "NS-2026-042",
              "objet": "Rappel des procédures d'inscription 2026-2027",
              "contenu": "Les responsables de formation sont priés de...",
              "dateEmission": "2026-05-10",
              "type": "INTERNE",
              "destinataires": "Tous les responsables de formation",
              "fichierJoint": null
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<NoteDeService> create(@RequestBody NoteDeService note) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(note));
    }

    @Operation(summary = "Modifier une note de service")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<NoteDeService> update(@PathVariable Long id, @RequestBody NoteDeService data) {
        return repo.findById(id).map(n -> {
            n.setReference(data.getReference());
            n.setObjet(data.getObjet());
            n.setContenu(data.getContenu());
            n.setDateEmission(data.getDateEmission());
            n.setType(data.getType());
            n.setDestinataires(data.getDestinataires());
            n.setFichierJoint(data.getFichierJoint());
            return ResponseEntity.ok(repo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une note de service", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}