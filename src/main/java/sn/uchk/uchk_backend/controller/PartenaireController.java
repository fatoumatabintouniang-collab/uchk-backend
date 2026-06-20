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
import sn.uchk.uchk_backend.entity.Partenaire;
import sn.uchk.uchk_backend.repository.PartenaireRepository;

import java.util.List;

@RestController
@RequestMapping("/api/partenaires")
@RequiredArgsConstructor
@Tag(name = "Appui à l'Insertion – Partenaires", description = "Gestion de la base de données des entreprises et organisations partenaires de l'université")
@SecurityRequirement(name = "bearerAuth")
public class PartenaireController {

    private final PartenaireRepository repo;

    @Operation(summary = "Lister tous les partenaires")
    @GetMapping
    public List<Partenaire> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Lister les partenaires actifs",
        description = "Retourne uniquement les partenaires dont le partenariat est en cours (actif = true).")
    @GetMapping("/actifs")
    public List<Partenaire> getActifs() {
        return repo.findByActif(true);
    }

    @Operation(summary = "Trouver un partenaire par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partenaire trouvé"),
        @ApiResponse(responseCode = "404", description = "Partenaire introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Partenaire> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un partenaire",
        description = "Enregistre un nouveau partenaire. Le typePartenariat peut être : STAGE, EMPLOI, FINANCEMENT, RECHERCHE, AUTRE.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du partenaire",
        content = @Content(examples = @ExampleObject(value = """
            {
              "nom": "Sonatel",
              "secteur": "Télécommunications",
              "adresse": "Route des Almadies, Dakar",
              "telephone": "+221 33 839 0000",
              "email": "partenariats@sonatel.sn",
              "siteWeb": "https://www.sonatel.com",
              "contactNom": "Ibrahima Sall",
              "contactPoste": "Directeur RH",
              "datePartenariat": "2024-01-10",
              "typePartenariat": "STAGE",
              "actif": true,
              "notes": "Accueil de 5 stagiaires par an"
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'APPUI_INSERTION')")
    public ResponseEntity<Partenaire> create(@RequestBody Partenaire partenaire) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(partenaire));
    }

    @Operation(summary = "Modifier un partenaire")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'APPUI_INSERTION')")
    public ResponseEntity<Partenaire> update(@PathVariable Long id, @RequestBody Partenaire data) {
        return repo.findById(id).map(p -> {
            p.setNom(data.getNom());
            p.setSecteur(data.getSecteur());
            p.setAdresse(data.getAdresse());
            p.setTelephone(data.getTelephone());
            p.setEmail(data.getEmail());
            p.setSiteWeb(data.getSiteWeb());
            p.setContactNom(data.getContactNom());
            p.setContactPoste(data.getContactPoste());
            p.setDatePartenariat(data.getDatePartenariat());
            p.setTypePartenariat(data.getTypePartenariat());
            p.setActif(data.getActif());
            p.setNotes(data.getNotes());
            return ResponseEntity.ok(repo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un partenaire", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}