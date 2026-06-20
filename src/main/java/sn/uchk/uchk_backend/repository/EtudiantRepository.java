package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Etudiant;
import java.util.Optional;
import java.util.List;
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByIne(String ine);
    List<Etudiant> findByFormationId(Long formationId);
    List<Etudiant> findByPromo(String promo);
}