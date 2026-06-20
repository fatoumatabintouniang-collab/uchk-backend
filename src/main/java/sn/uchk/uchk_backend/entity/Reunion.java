package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reunion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private LocalDateTime dateHeure;
    private String lieu;

    @Enumerated(EnumType.STRING)
    private TypeReunion typeReunion;

    private String participants;
    private String ordreJour;
    private String compteRendu;
    private Boolean effectuee;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    public enum TypeReunion {
        TUTORAT, PREPARATION_COURS, PREPARATION_EVALUATION, AUTRE
    }
}