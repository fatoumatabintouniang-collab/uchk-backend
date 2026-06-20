package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "autre_formation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AutreFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intitule;
    private String etablissement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private String certificat;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;
}