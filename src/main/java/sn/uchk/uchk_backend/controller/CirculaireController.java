package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.entity.Circulaire;
import sn.uchk.uchk_backend.repository.CirculaireRepository;

import java.util.List;

@RestController
@RequestMapping("/api/circulaires")
@RequiredArgsConstructor
@Tag(name = "Administration – Circulaires", description = "Gestion des circulaires institutionnelles")
@SecurityRequirement(name = "bearerAuth")
public class CirculaireController {

    private final CirculaireRepository repo;

    // ── Lecture : ADMIN, ADMINISTRATIF, ENSEIGNANT, ENSEIGNANT_ASSOCIE ──────
    @Operation(summary = "Lister toutes les circulaires")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Circulaire> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Filtrer par type")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public List<Circulaire> getByType(@PathVariable Circulaire.TypeCirculaire type) {
        return repo.findByType(type);
    }

    @Operation(summary = "Trouver une circulaire par ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE')")
    public ResponseEntity<Circulaire> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Écriture : ADMIN, ADMINISTRATIF uniquement ───────────────────────────
    @Operation(summary = "Créer une circulaire")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Circulaire> create(@RequestBody Circulaire circulaire) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(circulaire));
    }

    @Operation(summary = "Modifier une circulaire")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Circulaire> update(@PathVariable Long id, @RequestBody Circulaire data) {
        return repo.findById(id).map(c -> {
            c.setReference(data.getReference());
            c.setObjet(data.getObjet());
            c.setContenu(data.getContenu());
            c.setDateEmission(data.getDateEmission());
            c.setType(data.getType());
            c.setDestinataires(data.getDestinataires());
            c.setFichierJoint(data.getFichierJoint());
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une circulaire — réservé ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
