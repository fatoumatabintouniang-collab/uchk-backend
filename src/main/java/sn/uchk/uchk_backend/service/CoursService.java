package sn.uchk.uchk_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sn.uchk.uchk_backend.dto.CoursDTO;
import sn.uchk.uchk_backend.entity.Cours;
import sn.uchk.uchk_backend.entity.Formateur;
import sn.uchk.uchk_backend.entity.Formation;
import sn.uchk.uchk_backend.repository.CoursRepository;
import sn.uchk.uchk_backend.repository.FormateurRepository;
import sn.uchk.uchk_backend.repository.FormationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final CoursRepository coursRepository;
    private final FormationRepository formationRepository;
    private final FormateurRepository formateurRepository;

    // ── Lecture ──────────────────────────────────────────────────────────────

    public List<CoursDTO> getAll() {
        return coursRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CoursDTO> getByFormation(Long formationId) {
        return coursRepository.findByFormationId(formationId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CoursDTO> getByFormateur(Long formateurId) {
        return coursRepository.findByFormateurId(formateurId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CoursDTO getById(Long id) {
        return toDTO(findOrThrow(id));
    }

    // ── Création ─────────────────────────────────────────────────────────────

    public CoursDTO create(CoursDTO dto) {
        if (dto.getCode() != null && !dto.getCode().isBlank()
                && coursRepository.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un cours avec le code « " + dto.getCode() + " » existe déjà.");
        }
        Cours cours = toEntity(dto, new Cours());
        return toDTO(coursRepository.save(cours));
    }

    // ── Mise à jour ──────────────────────────────────────────────────────────

    public CoursDTO update(Long id, CoursDTO dto) {
        Cours cours = findOrThrow(id);
        if (dto.getCode() != null && !dto.getCode().isBlank()
                && coursRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Un autre cours utilise déjà le code « " + dto.getCode() + " ».");
        }
        toEntity(dto, cours);
        return toDTO(coursRepository.save(cours));
    }

    // ── Suppression ──────────────────────────────────────────────────────────

    public void delete(Long id) {
        if (!coursRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Cours introuvable avec l'id : " + id);
        }
        coursRepository.deleteById(id);
    }

    // ── Utilitaires privés ───────────────────────────────────────────────────

    private Cours findOrThrow(Long id) {
        return coursRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cours introuvable avec l'id : " + id));
    }

    /** Mappe un DTO vers l'entité (création ou mise à jour) */
    private Cours toEntity(CoursDTO dto, Cours cours) {
        cours.setIntitule(dto.getIntitule());
        cours.setCode(dto.getCode());
        cours.setDescription(dto.getDescription());
        cours.setVolumeHoraire(dto.getVolumeHoraire());
        cours.setCoefficient(dto.getCoefficient());
        cours.setSemestre(dto.getSemestre());
        cours.setActif(dto.getActif() != null ? dto.getActif() : true);

        // Formation (obligatoire)
        if (dto.getFormationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "formationId est obligatoire.");
        }
        Formation formation = formationRepository.findById(dto.getFormationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Formation introuvable avec l'id : " + dto.getFormationId()));
        cours.setFormation(formation);

        // Formateur (optionnel)
        if (dto.getFormateurId() != null) {
            Formateur formateur = formateurRepository.findById(dto.getFormateurId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Formateur introuvable avec l'id : " + dto.getFormateurId()));
            cours.setFormateur(formateur);
        } else {
            cours.setFormateur(null);
        }

        return cours;
    }

    /** Mappe une entité vers le DTO */
    public CoursDTO toDTO(Cours c) {
        CoursDTO dto = new CoursDTO();
        dto.setId(c.getId());
        dto.setIntitule(c.getIntitule());
        dto.setCode(c.getCode());
        dto.setDescription(c.getDescription());
        dto.setVolumeHoraire(c.getVolumeHoraire());
        dto.setCoefficient(c.getCoefficient());
        dto.setSemestre(c.getSemestre());
        dto.setActif(c.getActif());

        if (c.getFormation() != null) {
            dto.setFormationId(c.getFormation().getId());
            dto.setFormationIntitule(c.getFormation().getIntitule());
        }
        if (c.getFormateur() != null) {
            dto.setFormateurId(c.getFormateur().getId());
            dto.setFormateurNom(c.getFormateur().getPrenom() + " " + c.getFormateur().getNom());
        }
        return dto;
    }
}
