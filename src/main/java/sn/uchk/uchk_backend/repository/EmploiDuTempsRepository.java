package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.EmploiDuTemps;

import java.time.LocalDate;
import java.util.List;

/**
 * MODIFIÉ : ajout de findByCoursId pour l'architecture Formation → Cours → EmploiDuTemps.
 * Toutes les méthodes existantes sont conservées.
 */
public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {

    List<EmploiDuTemps> findByFormationId(Long formationId);

    List<EmploiDuTemps> findByFormateurId(Long formateurId);

    List<EmploiDuTemps> findByDate(LocalDate date);

    List<EmploiDuTemps> findByDateBetween(LocalDate debut, LocalDate fin);

    /** NOUVEAU : créneaux liés à un cours précis */
    List<EmploiDuTemps> findByCoursId(Long coursId);
}
