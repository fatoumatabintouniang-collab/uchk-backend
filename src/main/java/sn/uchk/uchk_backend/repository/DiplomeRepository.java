package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Diplome;
import java.util.List;

public interface DiplomeRepository extends JpaRepository<Diplome, Long> {
    List<Diplome> findByEtudiantId(Long etudiantId);
}