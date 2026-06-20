package sn.uchk.uchk_backend.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "formateur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String specialite;

    @Enumerated(EnumType.STRING)
    private TypeFormateur typeFormateur;

    private String grade;
    private Boolean actif;

    public enum TypeFormateur {
        ENSEIGNANT, ENSEIGNANT_ASSOCIE, RESPONSABLE_FORMATION, TUTEUR
    }
}