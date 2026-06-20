package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.Formateur;
import java.util.List;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
    List<Formateur> findByTypeFormateur(Formateur.TypeFormateur typeFormateur);
    List<Formateur> findByActif(Boolean actif);
}