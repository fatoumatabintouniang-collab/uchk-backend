package sn.uchk.uchk_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.uchk.uchk_backend.entity.Role;
import sn.uchk.uchk_backend.entity.Utilisateur;
import sn.uchk.uchk_backend.repository.RoleRepository;
import sn.uchk.uchk_backend.repository.UtilisateurRepository;

import java.util.List;

@SpringBootApplication
public class UchkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UchkBackendApplication.class, args);
    }

    /**
     * Initialise les rôles et un compte ADMIN par défaut au premier démarrage.
     * Si les données existent déjà, aucune action n'est effectuée (idempotent).
     */
    @Bean
    CommandLineRunner init(RoleRepository roleRepo,
                           UtilisateurRepository userRepo,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            // ── Création des rôles s'ils n'existent pas encore ──────────────
            List<String> roles = List.of(
                    "ADMIN", "ADMINISTRATIF", "ENSEIGNANT", "ENSEIGNANT_ASSOCIE",
                    "RESPONSABLE_FORMATION", "TUTEUR", "APPUI_INSERTION", "ETUDIANT"
            );
            for (String nom : roles) {
                if (roleRepo.findByNom(nom).isEmpty()) {
                    Role r = new Role();
                    r.setNom(nom);
                    roleRepo.save(r);
                    System.out.println("[UCHK] Rôle créé : " + nom);
                }
            }

            // ── Création du compte ADMIN par défaut si inexistant ───────────
            if (userRepo.findByEmail("admin@uchk.edu.sn").isEmpty()) {
                Role adminRole = roleRepo.findByNom("ADMIN").orElseThrow();
                Utilisateur admin = Utilisateur.builder()
                        .nom("Admin")
                        .prenom("UCHK")
                        .email("admin@uchk.edu.sn")
                        .ine(null)
                        .password(passwordEncoder.encode("Admin2026!"))
                        .actif(true)
                        .role(adminRole)
                        .build();
                userRepo.save(admin);
                System.out.println("[UCHK] Compte ADMIN créé : admin@uchk.edu.sn / Admin2026!");
            }

            System.out.println("[UCHK] Backend démarré avec succès !");
            System.out.println("Compte administrateur par défaut pour se connecter :\r\n" + //
                                "\r\n" + //
                                "Champ\tValeur\r\n" + //
                                "Email:\tadmin@uchk.edu.sn\r\n" + //
                                "Mot de passe:\tAdmin@2026\r\n" + //
                              "");


            System.out.println("[UCHK] Swagger : http://localhost:8080/swagger-ui/index.html");
        };
    }
}
