package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cours")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Intitulé de la matière / cours */
    @Column(nullable = false)
    private String intitule;

    /** Code unique du cours (ex: INFO301) */
    @Column(unique = true)
    private String code;

    /** Description pédagogique */
    private String description;

    /** Volume horaire total en heures */
    private Integer volumeHoraire;

    /** Coefficient pour les évaluations */
    private Double coefficient;

    /** Semestre ou période (ex: S1, S2, ANNUEL) */
    private String semestre;

    /** Cours actif ou archivé */
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "formation_id", nullable = false)
private Formation formation;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "formateur_id")
private Formateur formateur;
}
