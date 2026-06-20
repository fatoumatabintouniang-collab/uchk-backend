package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.AutreFormation;
import java.util.List;

public interface AutreFormationRepository extends JpaRepository<AutreFormation, Long> {
    List<AutreFormation> findByEtudiantId(Long etudiantId);
}