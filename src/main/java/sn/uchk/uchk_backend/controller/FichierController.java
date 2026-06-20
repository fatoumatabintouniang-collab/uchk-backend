package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fichiers")
@Tag(name = "Fichiers", description = "Upload et téléchargement de fichiers (photos, contrats, CV, documents joints)")
@SecurityRequirement(name = "bearerAuth")
public class FichierController {

    @Value("${fichiers.upload-dir:uploads}")
    private String uploadDir;

    @Operation(
        summary = "Uploader un fichier",
        description = """
            Upload un fichier sur le serveur et retourne son nom de stockage.
            
            Ce nom est ensuite utilisé dans les champs `photo`, `fichierContrat`, `fichierCV`, `fichierJoint` des entités.
            
            - Taille max recommandée : 10 Mo
            - Types acceptés : images (jpg, png), PDF, Word, Excel
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès"),
        @ApiResponse(responseCode = "400", description = "Fichier vide ou invalide"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur lors de l'upload")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF', 'RESPONSABLE_FORMATION', 'APPUI_INSERTION')")
    public ResponseEntity<?> upload(
            @Parameter(description = "Fichier à uploader") @RequestParam("fichier") MultipartFile fichier) {

        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide."));
        }

        try {
            Path dossier = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dossier);

            // Nom unique pour éviter les collisions
            String extension = "";
            String originalName = fichier.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String nomFichier = UUID.randomUUID() + extension;

            Path destination = dossier.resolve(nomFichier);
            Files.copy(fichier.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of(
                "nom", nomFichier,
                "nomOriginal", originalName != null ? originalName : "",
                "taille", fichier.getSize(),
                "url", "/api/fichiers/" + nomFichier
            ));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'upload : " + e.getMessage()));
        }
    }

    @Operation(
        summary = "Télécharger un fichier",
        description = "Retourne le fichier correspondant au nom fourni. Utilisé pour afficher les photos, télécharger les contrats, CV et documents joints."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier retourné"),
        @ApiResponse(responseCode = "404", description = "Fichier introuvable")
    })
    @GetMapping("/{nom}")
    public ResponseEntity<Resource> download(
            @Parameter(description = "Nom du fichier (retourné lors de l'upload)", example = "a1b2c3d4-xxxx.pdf")
            @PathVariable String nom) {

        try {
            Path fichierPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nom);
            Resource resource = new UrlResource(fichierPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(fichierPath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nom + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}