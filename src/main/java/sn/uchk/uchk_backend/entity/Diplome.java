package sn.uchk.uchk_backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diplome")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Diplome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intitule;
    private String etablissement;
    private LocalDate dateObtention;
    private String mention;
    private String fichier;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;
}