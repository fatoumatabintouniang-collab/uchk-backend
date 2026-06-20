package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "personnel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Identité ──────────────────────────────────────────────────────────────
    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private String photo;

    // ── Informations professionnelles ─────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private TypePersonnel type;          // ADMINISTRATIF, ENSEIGNANT, TUTEUR, TECHNIQUE

    private String poste;                // Intitulé du poste (ex: Secrétaire, Comptable)
    private String departement;          // Département ou service

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;     // CDI, CDD, VACATION, STAGE_PRO, BÉNÉVOLAT

    private LocalDate dateEmbauche;
    private LocalDate dateFinContrat;    // null si CDI ou en cours

    private Double salaire;
    private String numeroCNI;            // Carte Nationale d'Identité

    // ── Statut ────────────────────────────────────────────────────────────────
    private Boolean actif;

    // ── Documents ─────────────────────────────────────────────────────────────
    private String fichierContrat;       // Nom du fichier contrat stocké
    private String fichierCV;            // Nom du fichier CV stocké

    private String notes;

    public enum TypePersonnel {
        ADMINISTRATIF,
        ENSEIGNANT,
        TUTEUR,
        TECHNIQUE
    }

    public enum TypeContrat {
        CDI,
        CDD,
        VACATION,
        STAGE_PRO,
        BENEVOLAT
    }
}