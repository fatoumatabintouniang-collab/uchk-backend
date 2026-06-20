package sn.uchk.uchk_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.uchk.uchk_backend.dto.EtudiantDTO;
import sn.uchk.uchk_backend.entity.Etudiant;
import sn.uchk.uchk_backend.entity.Formation;
import sn.uchk.uchk_backend.repository.EtudiantRepository;
import sn.uchk.uchk_backend.repository.FormationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final FormationRepository formationRepository;

    public List<EtudiantDTO> findAll() {
        return etudiantRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EtudiantDTO findById(Long id) {
        return etudiantRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable avec l'id : " + id));
    }

    public EtudiantDTO findByIne(String ine) {
        return etudiantRepository.findByIne(ine)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable avec l'INE : " + ine));
    }

    public List<EtudiantDTO> findByFormation(Long formationId) {
        return etudiantRepository.findByFormationId(formationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EtudiantDTO create(EtudiantDTO dto) {
        Etudiant etudiant = toEntity(dto);
        Etudiant saved = etudiantRepository.save(etudiant);
        return toDTO(saved);
    }

    @Transactional
    public EtudiantDTO update(Long id, EtudiantDTO dto) {
        Etudiant existing = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable avec l'id : " + id));

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setDateNaissance(dto.getDateNaissance());
        existing.setPromo(dto.getPromo());
        existing.setAnneeDebut(dto.getAnneeDebut());
        existing.setAnneeSortie(dto.getAnneeSortie());
        existing.setTelephone(dto.getTelephone());
        existing.setEmail(dto.getEmail());
        existing.setAdresse(dto.getAdresse());
        existing.setPhoto(dto.getPhoto());

        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId())
                    .orElseThrow(() -> new RuntimeException("Formation introuvable avec l'id : " + dto.getFormationId()));
            existing.setFormation(formation);
        } else {
            existing.setFormation(null);
        }

        return toDTO(etudiantRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!etudiantRepository.existsById(id)) {
            throw new RuntimeException("Étudiant introuvable avec l'id : " + id);
        }
        etudiantRepository.deleteById(id);
    }

    // ─── Mapping Entité → DTO ──────────────────────────────────────────────────
    public EtudiantDTO toDTO(Etudiant e) {
        EtudiantDTO dto = new EtudiantDTO();
        dto.setId(e.getId());
        dto.setIne(e.getIne());
        dto.setNom(e.getNom());
        dto.setPrenom(e.getPrenom());
        dto.setDateNaissance(e.getDateNaissance());
        dto.setPromo(e.getPromo());
        dto.setAnneeDebut(e.getAnneeDebut());
        dto.setAnneeSortie(e.getAnneeSortie());
        dto.setTelephone(e.getTelephone());
        dto.setEmail(e.getEmail());
        dto.setAdresse(e.getAdresse());
        dto.setPhoto(e.getPhoto());

        if (e.getFormation() != null) {
            dto.setFormationId(e.getFormation().getId());
            dto.setFormationIntitule(e.getFormation().getIntitule());
        }

        return dto;
    }

    // ─── Mapping DTO → Entité ──────────────────────────────────────────────────
    private Etudiant toEntity(EtudiantDTO dto) {
        Etudiant etudiant = new Etudiant();
        etudiant.setIne(dto.getIne());
        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setPromo(dto.getPromo());
        etudiant.setAnneeDebut(dto.getAnneeDebut());
        etudiant.setAnneeSortie(dto.getAnneeSortie());
        etudiant.setTelephone(dto.getTelephone());
        etudiant.setEmail(dto.getEmail());
        etudiant.setAdresse(dto.getAdresse());
        etudiant.setPhoto(dto.getPhoto());

        if (dto.getFormationId() != null) {
            Formation formation = formationRepository.findById(dto.getFormationId())
                    .orElseThrow(() -> new RuntimeException("Formation introuvable avec l'id : " + dto.getFormationId()));
            etudiant.setFormation(formation);
        }

        return etudiant;
    }
}