package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "circulaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Circulaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;

    @Column(nullable = false)
    private String objet;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    private TypeCirculaire type;

    private String destinataires;

    private String fichierJoint;

    public enum TypeCirculaire {
        INFORMATION, DIRECTIVE, CONVOCATION, AUTRE
    }
}
