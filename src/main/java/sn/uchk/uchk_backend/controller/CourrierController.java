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
import sn.uchk.uchk_backend.entity.Courrier;
import sn.uchk.uchk_backend.repository.CourrierRepository;

import java.util.List;

@RestController
@RequestMapping("/api/courriers")
@RequiredArgsConstructor
@Tag(name = "Administration – Courriers", description = "Gestion du courrier arrivé et du courrier départ de l'université")
@SecurityRequirement(name = "bearerAuth")
public class CourrierController {

    private final CourrierRepository repo;

    @Operation(summary = "Lister tous les courriers",
        description = "Retourne tous les courriers enregistrés (arrivés et départs).")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Courrier> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Lister les courriers arrivés",
        description = "Retourne uniquement les courriers de type ARRIVE (reçus par l'université).")
    @GetMapping("/arrives")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Courrier> getArrivés() {
        return repo.findByType(Courrier.TypeCourrier.ARRIVE);
    }

    @Operation(summary = "Lister les courriers départ",
        description = "Retourne uniquement les courriers de type DEPART (envoyés par l'université).")
    @GetMapping("/departs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Courrier> getDéparts() {
        return repo.findByType(Courrier.TypeCourrier.DEPART);
    }

    @Operation(summary = "Trouver un courrier par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Courrier trouvé"),
        @ApiResponse(responseCode = "404", description = "Courrier introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Courrier> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Enregistrer un courrier",
        description = "Crée un nouveau courrier. Le champ 'type' doit être ARRIVE ou DEPART. Le champ 'statut' peut prendre les valeurs : EN_ATTENTE, TRAITE, ARCHIVE.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du courrier",
        content = @Content(examples = @ExampleObject(value = """
            {
              "reference": "UCHK-2026-001",
              "objet": "Demande d'information sur les inscriptions",
              "expediteur": "Ministère de l'Enseignement Supérieur",
              "destinataire": "Direction UCHK",
              "dateCourrier": "2026-05-01",
              "dateReception": "2026-05-03",
              "type": "ARRIVE",
              "fichierJoint": null,
              "statut": "EN_ATTENTE",
              "notes": "À transmettre au service des inscriptions"
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Courrier> create(@RequestBody Courrier courrier) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(courrier));
    }

    @Operation(summary = "Modifier un courrier")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<Courrier> update(@PathVariable Long id, @RequestBody Courrier data) {
        return repo.findById(id).map(c -> {
            c.setReference(data.getReference());
            c.setObjet(data.getObjet());
            c.setExpediteur(data.getExpediteur());
            c.setDestinataire(data.getDestinataire());
            c.setDateCourrier(data.getDateCourrier());
            c.setDateReception(data.getDateReception());
            c.setType(data.getType());
            c.setFichierJoint(data.getFichierJoint());
            c.setStatut(data.getStatut());
            c.setNotes(data.getNotes());
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un courrier", description = "Suppression définitive. Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}