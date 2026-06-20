package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "budget")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intitule;
    private Integer annee;

    @Enumerated(EnumType.STRING)
    private TypeBudget type;

    private Double montantPrevisionnel;
    private Double montantRealise;
    private String categorie;
    private String notes;
    private LocalDate dateCreation;

    public enum TypeBudget {
        PREVISIONNEL, REALISE
    }
}