package sn.uchk.uchk_backend.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "courrier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Courrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;
    private String objet;
    private String expediteur;
    private String destinataire;
    private LocalDate dateCourrier;
    private LocalDate dateReception;

    @Enumerated(EnumType.STRING)
    private TypeCourrier type;

    private String fichierJoint;
    private String statut;
    private String notes;

    public enum TypeCourrier {
        ARRIVE, DEPART
    }
}