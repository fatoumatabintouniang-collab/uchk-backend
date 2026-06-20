package sn.uchk.uchk_backend.dto;

import lombok.*;

/**
 * DTO pour la création / modification d'un Cours.
 * Utilise des IDs pour les relations afin d'éviter les problèmes de sérialisation.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CoursDTO {

    private Long id;

    /** Intitulé obligatoire */
    private String intitule;

    /** Code cours (ex: INFO301) */
    private String code;

    private String description;

    /** Volume horaire en heures */
    private Integer volumeHoraire;

    /** Coefficient */
    private Double coefficient;

    /** Semestre : S1, S2, ANNUEL, etc. */
    private String semestre;

    private Boolean actif;

    /** ID de la formation parente (obligatoire) */
    private Long formationId;

    /** Intitulé de la formation (lecture seule) */
    private String formationIntitule;

    /** ID du formateur responsable (optionnel) */
    private Long formateurId;

    /** Nom complet du formateur (lecture seule) */
    private String formateurNom;
}
