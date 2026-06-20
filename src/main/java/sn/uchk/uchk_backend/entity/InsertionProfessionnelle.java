package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "insertion_professionnelle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InsertionProfessionnelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeInsertion type;

    private String poste;
    private String employeur;
    private LocalDate datePrisePoste;
    private String secteurActivite;
    private String telephone;
    private String email;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    public enum TypeInsertion {
        EMPLOI_SALARIE, AUTO_EMPLOI, POURSUITE_ETUDES, SANS_EMPLOI
    }
}