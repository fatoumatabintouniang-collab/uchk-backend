package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.CompteRendu;
import java.util.List;

public interface CompteRenduRepository extends JpaRepository<CompteRendu, Long> {
    List<CompteRendu> findByTypeReunion(CompteRendu.TypeReunion typeReunion);
    List<CompteRendu> findByNotificationEnvoyee(Boolean notificationEnvoyee);
}