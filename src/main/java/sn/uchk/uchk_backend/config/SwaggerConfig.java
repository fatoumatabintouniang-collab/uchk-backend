package sn.uchk.uchk_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UCHK – API de Gestion Universitaire")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Université Cheikh Hamidou Kane")
                                .url("https://www.uchk.edu.sn"))
                        .description("""
                                ## API REST – Université Cheikh Hamidou Kane
                                
                                Cette API couvre la gestion administrative et pédagogique de l'université :
                                
                                - **Authentification** : connexion par email ou INE + mot de passe
                                - **Étudiants** : dossiers, diplômes, autres formations
                                - **Formateurs** : enseignants, enseignants associés, tuteurs, responsables
                                - **Formations** : programmes, emplois du temps, réunions pédagogiques
                                - **Administration** : courriers, notes de service, budgets, personnel RH
                                - **Communication** : comptes rendus, archivage, notifications
                                - **Appui à l'Insertion** : stages, insertions professionnelles, partenaires
                                - **Exports** : rapports Excel (.xlsx) et PDF (.pdf)
                                
                                ---
                                
                                ### Comment s'authentifier
                                
                                1. `POST /api/auth/login` avec votre **email** ou **INE** et votre mot de passe
                                2. Copiez le **token JWT** retourné
                                3. Cliquez sur **Authorize** 🔒 en haut à droite
                                4. Entrez : `Bearer <votre_token>` puis **Authorize**
                                5.
                                
                                ---
                                
                                ### Rôles disponibles
                                
                                | Rôle | Accès |
                                |------|-------|
                                | `ADMIN` | Accès total (super utilisateur) |
                                | `ADMINISTRATIF` | Documents, étudiants, personnel, budgets |
                                | `ENSEIGNANT` | Comptes rendus, réunions pédagogiques |
                                | `ENSEIGNANT_ASSOCIE` | Comptes rendus, réunions pédagogiques |
                                | `RESPONSABLE_FORMATION` | Formations, emplois du temps |
                                | `TUTEUR` | Suivi des stages |
                                | `APPUI_INSERTION` | Stages, insertions, partenaires |
                                | `ETUDIANT` | Consultation de son propre dossier |
                                """))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Serveur local de développement")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenu via POST /api/auth/login. Format : Bearer <token>")));
    }
}
