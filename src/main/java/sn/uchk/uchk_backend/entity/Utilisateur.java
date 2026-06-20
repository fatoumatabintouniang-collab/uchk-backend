package sn.uchk.uchk_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    // INE optionnel : renseigné pour les étudiants, null pour le personnel
    @Column(unique = true, nullable = true)
    private String ine;

    // FIX: @JsonIgnore pour ne jamais exposer le hash du mot de passe dans les réponses API
    @JsonIgnore
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "Mot de passe (jamais retourné)")
    @Column(name = "mot_de_passe")
    private String password;

    private Boolean actif;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
