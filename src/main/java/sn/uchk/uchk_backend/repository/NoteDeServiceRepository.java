package sn.uchk.uchk_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uchk.uchk_backend.entity.NoteDeService;
import java.util.List;

public interface NoteDeServiceRepository extends JpaRepository<NoteDeService, Long> {
    List<NoteDeService> findByType(NoteDeService.TypeNote type);
    List<NoteDeService> findByAuteurId(Long auteurId);
}