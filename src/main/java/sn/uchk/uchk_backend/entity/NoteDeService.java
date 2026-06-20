package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "note_de_service")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoteDeService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;
    private String objet;
    private String contenu;
    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    private TypeNote type;

    private String destinataires;
    private String fichierJoint;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    public enum TypeNote {
        INTERNE, EXTERNE, ADMINISTRATIVE, CIRCULAIRE
    }
}