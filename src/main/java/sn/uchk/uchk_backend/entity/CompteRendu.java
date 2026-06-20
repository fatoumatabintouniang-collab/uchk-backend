package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "compte_rendu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompteRendu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeReunion typeReunion;

    private LocalDate dateReunion;
    private String lieu;
    private String contenu;
    private String fichierJoint;
    private Boolean notificationEnvoyee;

    @ManyToOne
    @JoinColumn(name = "redacteur_id")
    private Utilisateur redacteur;

    public enum TypeReunion {
        REUNION, RENCONTRE, SEMINAIRE, WEBINAIRE, CONSEIL_UNIVERSITE, AUTRE
    }
}