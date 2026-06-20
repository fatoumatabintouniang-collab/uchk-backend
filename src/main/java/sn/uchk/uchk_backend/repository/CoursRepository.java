package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Cours;

import java.util.List;
import java.util.Optional;

public interface CoursRepository extends JpaRepository<Cours, Long> {

    /** Tous les cours d'une formation */
    List<Cours> findByFormationId(Long formationId);

    /** Cours d'une formation filtrés par semestre */
    List<Cours> findByFormationIdAndSemestre(Long formationId, String semestre);

    /** Cours actifs d'une formation */
    List<Cours> findByFormationIdAndActif(Long formationId, Boolean actif);

    /** Cours assignés à un formateur */
    List<Cours> findByFormateurId(Long formateurId);

    /** Recherche par code unique */
    Optional<Cours> findByCode(String code);

    /** Vérifier si un code existe déjà */
    boolean existsByCode(String code);

    /** Vérifier si un code existe pour un autre cours (lors d'une mise à jour) */
    boolean existsByCodeAndIdNot(String code, Long id);
}
