package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.service.ExportService;

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
@Tag(name = "Exports", description = "Génération et téléchargement de rapports en format Excel (.xlsx) et PDF (.pdf)")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {

    private final ExportService exportService;

    // ── EXCEL ──────────────────────────────────────────────────────────────────

    @Operation(summary = "Exporter la liste des étudiants en Excel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier Excel généré"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/etudiants/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<byte[]> exportEtudiantsExcel() {
        byte[] data = exportService.exportEtudiantsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etudiants_uchk.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @Operation(summary = "Exporter les insertions professionnelles en Excel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier Excel généré"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/insertions/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPUI_INSERTION')")
    public ResponseEntity<byte[]> exportInsertionsExcel() {
        byte[] data = exportService.exportInsertionsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=insertions_professionnelles_uchk.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    // ── PDF ────────────────────────────────────────────────────────────────────

    @Operation(summary = "Exporter la liste des étudiants en PDF")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier PDF généré"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/etudiants/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIF')")
    public ResponseEntity<byte[]> exportEtudiantsPdf() {
        byte[] data = exportService.exportEtudiantsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etudiants_uchk.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @Operation(summary = "Exporter les insertions professionnelles en PDF")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fichier PDF généré"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/insertions/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'APPUI_INSERTION')")
    public ResponseEntity<byte[]> exportInsertionsPdf() {
        byte[] data = exportService.exportInsertionsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=insertions_professionnelles_uchk.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}