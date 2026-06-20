package sn.uchk.uchk_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.uchk.uchk_backend.dto.LoginRequest;
import sn.uchk.uchk_backend.dto.RegisterRequest;
import sn.uchk.uchk_backend.entity.Role;
import sn.uchk.uchk_backend.entity.Utilisateur;
import sn.uchk.uchk_backend.repository.RoleRepository;
import sn.uchk.uchk_backend.repository.UtilisateurRepository;
import sn.uchk.uchk_backend.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Connexion : accepte un email ou un INE + mot de passe.
     *
     * FIX : on cherche d'abord par email (normalisé lowercase), puis par INE (uppercase).
     * Évite la NonUniqueResultException quand plusieurs utilisateurs ont ine=NULL.
     */
    public String login(LoginRequest request) {
        if (request.getIdentifiant() == null || request.getIdentifiant().isBlank()) {
            throw new RuntimeException("L'identifiant (email ou INE) est requis.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Le mot de passe est requis.");
        }

        String identifiant = request.getIdentifiant().trim();

        // FIX: recherche email en lowercase, puis INE en uppercase
        Utilisateur utilisateur = utilisateurRepository.findByEmail(identifiant.toLowerCase())
                .or(() -> utilisateurRepository.findByIne(identifiant.toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Identifiant ou mot de passe incorrect."));

        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new RuntimeException("Ce compte est désactivé. Contactez l'administration.");
        }

        if (!passwordEncoder.matches(request.getPassword(), utilisateur.getPassword())) {
            throw new RuntimeException("Identifiant ou mot de passe incorrect.");
        }

        String roleNom = utilisateur.getRole() != null ? utilisateur.getRole().getNom() : "ETUDIANT";
        return jwtUtil.generateToken(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                roleNom,
                utilisateur.getIne()
        );
    }

    /**
     * Inscription : crée un nouveau compte utilisateur.
     *
     * FIX :
     * - Email normalisé en lowercase avant vérification ET sauvegarde
     * - INE normalisé en uppercase
     * - findByIne() utilise une requête JPQL IS NOT NULL → null-safe
     * - Le rôle est créé automatiquement s'il n'existe pas encore
     */
    public String register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("L'adresse email est obligatoire.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères.");
        }

        // FIX: normalisation avant recherche
        String emailNorm = request.getEmail().trim().toLowerCase();

        if (utilisateurRepository.findByEmail(emailNorm).isPresent()) {
            throw new RuntimeException("Cette adresse email est déjà utilisée.");
        }

        String roleNom = request.getRoleNom() != null
                ? request.getRoleNom().trim().toUpperCase()
                : "ETUDIANT";

        if ("ETUDIANT".equals(roleNom)) {
            if (request.getIne() == null || request.getIne().isBlank()) {
                throw new RuntimeException("Le numéro INE est obligatoire pour un compte étudiant.");
            }
            String ineNorm = request.getIne().trim().toUpperCase();
            if (utilisateurRepository.findByIne(ineNorm).isPresent()) {
                throw new RuntimeException("Ce numéro INE est déjà associé à un compte.");
            }
        }

        Role role = roleRepository.findByNom(roleNom)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setNom(roleNom);
                    return roleRepository.save(r);
                });

        String ineValue = (request.getIne() != null && !request.getIne().isBlank())
                ? request.getIne().trim().toUpperCase()
                : null;

        Utilisateur nouvel = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(emailNorm)
                .ine(ineValue)
                .password(passwordEncoder.encode(request.getPassword()))
                .actif(true)
                .role(role)
                .build();

        utilisateurRepository.save(nouvel);

        return "Compte créé avec succès pour "
                + request.getPrenom() + " " + request.getNom()
                + " (rôle : " + role.getNom() + ").";
    }
}
