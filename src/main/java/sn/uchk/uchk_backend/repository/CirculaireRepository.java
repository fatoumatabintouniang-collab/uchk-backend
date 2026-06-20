package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Circulaire;
import java.util.List;

public interface CirculaireRepository extends JpaRepository<Circulaire, Long> {
    List<Circulaire> findByType(Circulaire.TypeCirculaire type);
}
