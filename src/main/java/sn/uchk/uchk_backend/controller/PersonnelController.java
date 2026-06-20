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
import sn.uchk.uchk_backend.entity.Personnel;
import sn.uchk.uchk_backend.repository.PersonnelRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
@Tag(
    name = "Administration – Dossiers RH",
    description = "Gestion des dossiers du personnel : administratif, enseignants, tuteurs et personnel technique"
)
@SecurityRequirement(name = "bearerAuth")
public class PersonnelController {

    private final PersonnelRepository repo;

    @Operation(summary = "Lister tout le personnel",
        description = "Retourne tous les dossiers RH. Accessible à ADMIN et ADMINISTRATIF.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Personnel> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Lister le personnel actif",
        description = "Retourne uniquement les membres du personnel dont le contrat est en cours (actif = true).")
    @GetMapping("/actifs")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Personnel> getActifs() {
        return repo.findByActif(true);
    }

    @Operation(
        summary = "Filtrer par type de personnel",
        description = "Types disponibles : `ADMINISTRATIF`, `ENSEIGNANT`, `TUTEUR`, `TECHNIQUE`"
    )
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Personnel> getByType(
            @Parameter(description = "Type de personnel", example = "ADMINISTRATIF")
            @PathVariable Personnel.TypePersonnel type) {
        return repo.findByType(type);
    }

    @Operation(summary = "Filtrer par département / service",
        description = "Exemples : Scolarité, Comptabilité, Informatique, Direction")
    @GetMapping("/departement/{departement}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Personnel> getByDepartement(
            @Parameter(description = "Nom du département", example = "Scolarité")
            @PathVariable String departement) {
        return repo.findByDepartement(departement);
    }

    @Operation(summary = "Filtrer par type de contrat",
        description = "Types : `CDI`, `CDD`, `VACATION`, `STAGE_PRO`, `BENEVOLAT`")
    @GetMapping("/contrat/{typeContrat}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public List<Personnel> getByContrat(
            @Parameter(description = "Type de contrat", example = "CDD")
            @PathVariable Personnel.TypeContrat typeContrat) {
        return repo.findByTypeContrat(typeContrat);
    }

    @Operation(summary = "Trouver un dossier par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dossier trouvé"),
        @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> getById(
            @Parameter(description = "ID du membre du personnel", example = "1")
            @PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Dossier introuvable avec l'id : " + id)));
    }

    @Operation(
        summary = "Créer un dossier RH",
        description = """
            Enregistre un nouveau membre du personnel.
            
            - **type** : `ADMINISTRATIF`, `ENSEIGNANT`, `TUTEUR`, `TECHNIQUE`
            - **typeContrat** : `CDI`, `CDD`, `VACATION`, `STAGE_PRO`, `BENEVOLAT`
            - **dateFinContrat** : laisser `null` pour les CDI ou contrats en cours
            - **fichierContrat / fichierCV** : noms des fichiers uploadés via `POST /api/fichiers/upload`
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "Agent administratif", value = """
                {
                  "nom": "Fall",
                  "prenom": "Aminata",
                  "email": "a.fall@uchk.edu.sn",
                  "telephone": "+221 77 222 3344",
                  "adresse": "Dakar, Sénégal",
                  "dateNaissance": "1985-03-20",
                  "type": "ADMINISTRATIF",
                  "poste": "Secrétaire de direction",
                  "departement": "Direction générale",
                  "typeContrat": "CDI",
                  "dateEmbauche": "2018-09-01",
                  "dateFinContrat": null,
                  "salaire": 350000,
                  "numeroCNI": "1 234 567 890 12",
                  "actif": true,
                  "notes": "Responsable de l'accueil et du courrier"
                }
            """),
            @ExampleObject(name = "Enseignant vacataire", value = """
                {
                  "nom": "Sow",
                  "prenom": "Ibrahima",
                  "email": "i.sow@uchk.edu.sn",
                  "telephone": "+221 76 555 6677",
                  "adresse": "Thiès, Sénégal",
                  "dateNaissance": "1979-11-05",
                  "type": "ENSEIGNANT",
                  "poste": "Chargé de cours",
                  "departement": "Informatique",
                  "typeContrat": "VACATION",
                  "dateEmbauche": "2024-10-01",
                  "dateFinContrat": "2025-07-31",
                  "salaire": 150000,
                  "numeroCNI": "9 876 543 210 98",
                  "actif": true,
                  "notes": "Spécialiste en bases de données"
                }
            """)
        })
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dossier créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà existant")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> create(@RequestBody Personnel personnel) {
        try {
            if (personnel.getEmail() != null
                    && repo.findByEmail(personnel.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Un dossier avec cet email existe déjà."));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(personnel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Modifier un dossier RH")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dossier mis à jour"),
        @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<?> update(
            @Parameter(description = "ID du membre du personnel") @PathVariable Long id,
            @RequestBody Personnel data) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(p -> {          // ← fix ligne 204
                    p.setNom(data.getNom());
                    p.setPrenom(data.getPrenom());
                    p.setEmail(data.getEmail());
                    p.setTelephone(data.getTelephone());
                    p.setAdresse(data.getAdresse());
                    p.setDateNaissance(data.getDateNaissance());
                    p.setPhoto(data.getPhoto());
                    p.setType(data.getType());
                    p.setPoste(data.getPoste());
                    p.setDepartement(data.getDepartement());
                    p.setTypeContrat(data.getTypeContrat());
                    p.setDateEmbauche(data.getDateEmbauche());
                    p.setDateFinContrat(data.getDateFinContrat());
                    p.setSalaire(data.getSalaire());
                    p.setNumeroCNI(data.getNumeroCNI());
                    p.setActif(data.getActif());
                    p.setFichierContrat(data.getFichierContrat());
                    p.setFichierCV(data.getFichierCV());
                    p.setNotes(data.getNotes());
                    return ResponseEntity.ok(repo.save(p));
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Dossier introuvable avec l'id : " + id)));
    }

    @Operation(summary = "Activer ou désactiver un dossier",
        description = "Désactiver un dossier indique que la personne n'est plus en poste.")
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleActif(
            @Parameter(description = "ID du membre du personnel") @PathVariable Long id,
            @Parameter(description = "true = actif, false = inactif") @RequestParam Boolean actif) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(p -> {          // ← fix ligne 218
                    p.setActif(actif);
                    return ResponseEntity.ok(repo.save(p));
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Dossier introuvable avec l'id : " + id)));
    }

    @Operation(summary = "Supprimer un dossier RH", description = "Suppression définitive. Réservé à l'ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Dossier supprimé"),
        @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID du membre du personnel") @PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Dossier introuvable avec l'id : " + id));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}