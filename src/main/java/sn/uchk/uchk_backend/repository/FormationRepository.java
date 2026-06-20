package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Formation;

import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByActive(Boolean active);
    List<Formation> findByNiveau(String niveau);
    List<Formation> findByType(Formation.TypeFormation type);
}