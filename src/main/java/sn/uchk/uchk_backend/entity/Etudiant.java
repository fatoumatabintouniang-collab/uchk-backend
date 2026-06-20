package sn.uchk.uchk_backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "etudiant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ine;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String promo;
    private Integer anneeDebut;
    private Integer anneeSortie;
    private String telephone;
    private String email;
    private String adresse;
    private String photo;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;
}