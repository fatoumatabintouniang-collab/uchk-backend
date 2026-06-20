package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.uchk.uchk_backend.entity.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // FIX: recherche insensible à la casse pour email
    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Utilisateur> findByEmail(@Param("email") String email);

    /**
     * Recherche par INE — on exclut explicitement les valeurs NULL pour éviter
     * toute NonUniqueResultException quand N utilisateurs ont ine=NULL.
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.ine IS NOT NULL AND UPPER(u.ine) = UPPER(:ine)")
    Optional<Utilisateur> findByIne(@Param("ine") String ine);

    List<Utilisateur> findByActif(Boolean actif);

    List<Utilisateur> findByRoleNom(String roleNom);
}
