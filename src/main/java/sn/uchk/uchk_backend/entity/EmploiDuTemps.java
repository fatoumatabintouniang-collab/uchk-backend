package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MODIFICATION : ajout du champ {@code cours} (ManyToOne vers Cours)
 * et du champ {@code pdfEmploiDuTemps} pour stocker le PDF uploadé.
 * Tous les champs existants sont conservés à l'identique.
 */
@Entity
@Table(name = "emploi_du_temps")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmploiDuTemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Matière (libellé libre — conservé pour rétrocompatibilité) */
    private String matiere;

    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;

    @Enumerated(EnumType.STRING)
    private TypeSeance typeSeance;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne
    @JoinColumn(name = "formateur_id")
    private Formateur formateur;

    /**
     * NOUVEAU : référence vers l'entité Cours.
     * Permet l'architecture Formation → Cours → EmploiDuTemps.
     * Nullable pour garder la compatibilité avec les créneaux existants.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id")
    private Cours cours;

    /**
     * NOUVEAU : nom du fichier PDF de l'emploi du temps uploadé sur le serveur.
     * Stocké via /api/fichiers/upload (FichierController existant).
     */
    private String pdfEmploiDuTemps;

    public enum TypeSeance {
        COURS, DEVOIR, EXAMEN, TUTORAT
    }
}
