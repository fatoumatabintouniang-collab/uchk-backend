package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "formation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intitule;

    @Enumerated(EnumType.STRING)
    private TypeFormation type;

    private String niveau;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Double montantFinancement;

    @Enumerated(EnumType.STRING)
    private TypeFinancement typeFinancement;

    // Nombre de formés ventilé par genre (requis par l'énoncé)
    private Integer nombreFormesHomme;
    private Integer nombreFormesFemme;

    private String description;
    private Boolean active;

    // Nombre total calculé (non stocké en base)
    @Transient
    public Integer getNombreFormesTotal() {
        int h = nombreFormesHomme != null ? nombreFormesHomme : 0;
        int f = nombreFormesFemme != null ? nombreFormesFemme : 0;
        return h + f;
    }

    public enum TypeFormation {
        LICENCE, MASTER, DUT, BTS, FORMATION_CONTINUE, CERTIFICATION
    }

    public enum TypeFinancement {
        PUBLIC, PRIVE, CERTIFICATION, PARTENARIAT
    }
}