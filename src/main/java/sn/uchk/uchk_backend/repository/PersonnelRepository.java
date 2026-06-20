package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Personnel;

import java.util.List;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    List<Personnel> findByType(Personnel.TypePersonnel type);

    List<Personnel> findByActif(Boolean actif);

    List<Personnel> findByDepartement(String departement);

    List<Personnel> findByTypeContrat(Personnel.TypeContrat typeContrat);

    Optional<Personnel> findByEmail(String email);
}