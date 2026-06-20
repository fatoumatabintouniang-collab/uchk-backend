package sn.uchk.uchk_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.uchk.uchk_backend.dto.LoginRequest;
import sn.uchk.uchk_backend.dto.LoginResponse;
import sn.uchk.uchk_backend.dto.RegisterRequest;
import sn.uchk.uchk_backend.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Connexion et création de comptes utilisateurs")
public class AuthController {

    private final AuthService service;

    @Operation(
        summary = "Connexion",
        description = """
            Authentifie un utilisateur et retourne un token JWT.
            
            **Identifiant accepté :**
            - Email universitaire : `fatou.diallo@uchk.edu.sn`
            - Email personnel : `fatou.diallo@gmail.com`
            - INE (étudiants uniquement) : `INE2026001`
            
            Le token retourné doit être transmis dans toutes les requêtes suivantes via le header :
            `Authorization: Bearer <token>`
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Identifiant (email ou INE) + mot de passe",
        content = @Content(examples = {
            @ExampleObject(name = "Connexion par email", value = """
                {
                  "identifiant": "fatou.diallo@uchk.edu.sn",
                  "password": "motdepasse123"
                }
            """),
            @ExampleObject(name = "Connexion par INE", value = """
                {
                  "identifiant": "INE2026001",
                  "password": "motdepasse123"
                }
            """)
        })
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie — token JWT retourné",
            content = @Content(schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9...\"}"))),
        @ApiResponse(responseCode = "401", description = "Identifiant ou mot de passe incorrect",
            content = @Content(examples = @ExampleObject(
                value = "{\"error\": \"Identifiant ou mot de passe incorrect.\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = service.login(request);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
        summary = "Créer un compte",
        description = """
            Crée un nouveau compte utilisateur.
            
            - **Email** : obligatoire pour tous les rôles
            - **INE** : obligatoire uniquement pour le rôle `ETUDIANT`
            - **Rôles disponibles** : `ADMIN`, `ADMINISTRATIF`, `ENSEIGNANT`, `ENSEIGNANT_ASSOCIE`,
              `RESPONSABLE_FORMATION`, `TUTEUR`, `APPUI_INSERTION`, `ETUDIANT`
            
            Après inscription, utilisez `POST /api/auth/login` pour obtenir votre token.
            """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Données du nouveau compte",
        content = @Content(examples = {
            @ExampleObject(name = "Compte étudiant (avec INE)", value = """
                {
                  "nom": "Diallo",
                  "prenom": "Fatou",
                  "email": "fatou.diallo@uchk.edu.sn",
                  "ine": "INE2026001",
                  "password": "motdepasse123",
                  "roleNom": "ETUDIANT"
                }
            """),
            @ExampleObject(name = "Compte enseignant (sans INE)", value = """
                {
                  "nom": "Ndiaye",
                  "prenom": "Mamadou",
                  "email": "m.ndiaye@uchk.edu.sn",
                  "ine": null,
                  "password": "motdepasse123",
                  "roleNom": "ENSEIGNANT"
                }
            """)
        })
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compte créé avec succès",
            content = @Content(examples = @ExampleObject(
                value = "{\"message\": \"Compte créé avec succès pour Fatou Diallo (rôle : ETUDIANT).\"}"))),
        @ApiResponse(responseCode = "400", description = "Données invalides ou email/INE déjà existant",
            content = @Content(examples = @ExampleObject(
                value = "{\"error\": \"Cette adresse email est déjà utilisée.\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            String message = service.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}