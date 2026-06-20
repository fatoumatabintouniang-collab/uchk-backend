package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Courrier;
import java.util.List;

public interface CourrierRepository extends JpaRepository<Courrier, Long> {
    List<Courrier> findByType(Courrier.TypeCourrier type);
    List<Courrier> findByStatut(String statut);
}