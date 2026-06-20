package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.InsertionProfessionnelle;
import java.util.List;

public interface InsertionProfessionnelleRepository extends JpaRepository<InsertionProfessionnelle, Long> {
    List<InsertionProfessionnelle> findByEtudiantId(Long etudiantId);
    List<InsertionProfessionnelle> findByType(InsertionProfessionnelle.TypeInsertion type);
    long countByType(InsertionProfessionnelle.TypeInsertion type);
}