package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "stage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String entreprise;
    private String adresseEntreprise;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutStage statut;

    private String rapport;
    private Double note;
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "tuteur_id")
    private Formateur tuteur;

    public enum StatutStage {
        EN_COURS, TERMINE, VALIDE, REFUSE
    }
}