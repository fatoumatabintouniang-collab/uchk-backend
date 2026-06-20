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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.uchk.uchk_backend.entity.EmploiDuTemps;
import sn.uchk.uchk_backend.repository.CoursRepository;
import sn.uchk.uchk_backend.repository.EmploiDuTempsRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MODIFIÉ : ajout de l'endpoint /upload-pdf et du champ cours dans create/update.
 * Tous les endpoints existants sont conservés à l'identique.
 */
@RestController
@RequestMapping("/api/emplois-du-temps")
@RequiredArgsConstructor
@Tag(name = "Formations – Emplois du Temps", description = "Gestion des emplois du temps : cours, devoirs, examens et séances de tutorat")
@SecurityRequirement(name = "bearerAuth")
public class EmploiDuTempsController {

    private final EmploiDuTempsRepository repo;
    private final CoursRepository coursRepository;

    @Value("${fichiers.upload-dir:uploads}")
    private String uploadDir;

    // ── Endpoints existants (inchangés) ──────────────────────────────────────

    @Operation(summary = "Lister tous les créneaux")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<EmploiDuTemps> getAll() {
        return repo.findAll();
    }

    @Operation(summary = "Emplois du temps d'une formation")
    @GetMapping("/formation/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<EmploiDuTemps> getByFormation(@PathVariable Long id) {
        return repo.findByFormationId(id);
    }

    @Operation(summary = "Emplois du temps d'un formateur")
    @GetMapping("/formateur/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<EmploiDuTemps> getByFormateur(@PathVariable Long id) {
        return repo.findByFormateurId(id);
    }

    @Operation(summary = "Filtrer les créneaux par période")
    @GetMapping("/periode")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<EmploiDuTemps> getByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return repo.findByDateBetween(debut, fin);
    }

    /**
     * NOUVEAU : Créneaux liés à un cours précis.
     */
    @Operation(summary = "Créneaux liés à un cours",
            description = "Retourne tous les créneaux d'emploi du temps rattachés à un cours donné.")
    @GetMapping("/cours/{coursId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public List<EmploiDuTemps> getByCours(@PathVariable Long coursId) {
        return repo.findByCoursId(coursId);
    }

    @Operation(summary = "Trouver un créneau par ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'ENSEIGNANT', 'ENSEIGNANT_ASSOCIE', 'ETUDIANT')")
    public ResponseEntity<EmploiDuTemps> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un créneau dans l'emploi du temps",
            description = "Champs NOUVEAUX : coursId (optionnel) pour lier le créneau à un cours.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
            {
              "matiere": "Algorithmique Avancée",
              "date": "2026-05-28",
              "heureDebut": "08:00:00",
              "heureFin": "11:00:00",
              "salle": "Salle Info A",
              "typeSeance": "COURS",
              "formation": { "id": 1 },
              "formateur": { "id": 2 },
              "cours": { "id": 3 }
            }
        """))
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<EmploiDuTemps> create(@RequestBody EmploiDuTemps edt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(edt));
    }

    @Operation(summary = "Modifier un créneau")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<EmploiDuTemps> update(@PathVariable Long id, @RequestBody EmploiDuTemps data) {
        return repo.findById(id).map(e -> {
            e.setMatiere(data.getMatiere());
            e.setDate(data.getDate());
            e.setHeureDebut(data.getHeureDebut());
            e.setHeureFin(data.getHeureFin());
            e.setSalle(data.getSalle());
            e.setTypeSeance(data.getTypeSeance());
            e.setFormation(data.getFormation());
            e.setFormateur(data.getFormateur());
            e.setCours(data.getCours());           // NOUVEAU
            // pdfEmploiDuTemps géré par /upload-pdf
            return ResponseEntity.ok(repo.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un créneau", description = "Réservé à l'ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── NOUVEAU : Upload PDF emploi du temps ──────────────────────────────────

    @Operation(
            summary = "Uploader le PDF d'un emploi du temps",
            description = """
            Upload le fichier PDF de l'emploi du temps pour un créneau donné
            et enregistre son nom dans le champ `pdfEmploiDuTemps`.
            
            - Méthode : POST multipart/form-data
            - Param : `fichier` (MultipartFile, PDF uniquement)
            - Le PDF est téléchargeable ensuite via /api/fichiers/{nom}
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF uploadé et lié au créneau"),
            @ApiResponse(responseCode = "400", description = "Fichier vide ou créneau introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping(value = "/{id}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION')")
    public ResponseEntity<?> uploadPdf(
            @Parameter(description = "ID du créneau", example = "1") @PathVariable Long id,
            @Parameter(description = "Fichier PDF à uploader") @RequestParam("fichier") MultipartFile fichier) {

        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide."));
        }

        EmploiDuTemps edt = repo.findById(id).orElse(null);
        if (edt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Créneau introuvable avec l'id : " + id));
        }

        try {
            Path dossier = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dossier);

            String extension = "";
            String originalName = fichier.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String nomFichier = "edt_" + id + "_" + UUID.randomUUID() + extension;
            Path destination = dossier.resolve(nomFichier);
            Files.copy(fichier.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            edt.setPdfEmploiDuTemps(nomFichier);
            repo.save(edt);

            return ResponseEntity.ok(Map.of(
                    "nom", nomFichier,
                    "url", "/api/fichiers/" + nomFichier,
                    "message", "PDF lié au créneau #" + id
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'upload : " + e.getMessage()));
        }
    }
}
