package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Reunion;
import java.util.List;

public interface ReunionRepository extends JpaRepository<Reunion, Long> {
    List<Reunion> findByFormationId(Long formationId);
    List<Reunion> findByTypeReunion(Reunion.TypeReunion typeReunion);
    List<Reunion> findByEffectuee(Boolean effectuee);
}