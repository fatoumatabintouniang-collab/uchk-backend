package sn.uchk.uchk_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Dossier complet d'un étudiant")
public class EtudiantDTO {

    @Schema(description = "Identifiant interne", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Identifiant National Étudiant (INE)", example = "SN2024001")
    private String ine;

    @Schema(description = "Nom de famille", example = "Diallo")
    private String nom;

    @Schema(description = "Prénom", example = "Fatou")
    private String prenom;

    @Schema(description = "Date de naissance (format YYYY-MM-DD)", example = "2000-05-15")
    private LocalDate dateNaissance;

    @Schema(description = "Promotion (année d'entrée)", example = "2024")
    private String promo;

    @Schema(description = "Année de début de formation", example = "2024")
    private Integer anneeDebut;

    @Schema(description = "Année de sortie (null si encore en formation)", example = "2026")
    private Integer anneeSortie;

    @Schema(description = "Numéro de téléphone", example = "+221 77 000 0000")
    private String telephone;

    @Schema(description = "Adresse email de l'étudiant", example = "fatou.diallo@uchk.sn")
    private String email;

    @Schema(description = "Adresse postale", example = "Dakar, Sénégal")
    private String adresse;

    @Schema(description = "URL ou chemin vers la photo de profil", example = "photos/diallo_fatou.jpg")
    private String photo;

    @Schema(description = "ID de la formation dans laquelle est inscrit l'étudiant", example = "1")
    private Long formationId;

    @Schema(description = "Intitulé de la formation (lecture seule)", example = "Master Ingénierie Logicielle", accessMode = Schema.AccessMode.READ_ONLY)
    private String formationIntitule;
}