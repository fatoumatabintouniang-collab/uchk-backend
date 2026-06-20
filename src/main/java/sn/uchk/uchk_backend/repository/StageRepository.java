package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Stage;
import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findByEtudiantId(Long etudiantId);
    List<Stage> findByTuteurId(Long tuteurId);
    List<Stage> findByStatut(Stage.StatutStage statut);
}