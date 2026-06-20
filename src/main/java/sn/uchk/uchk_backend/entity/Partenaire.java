package sn.uchk.uchk_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "partenaire")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Partenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String secteur;
    private String adresse;
    private String telephone;
    private String email;
    private String siteWeb;
    private String contactNom;
    private String contactPoste;
    private LocalDate datePartenariat;
    private String typePartenariat;
    private Boolean actif;
    private String notes;
}