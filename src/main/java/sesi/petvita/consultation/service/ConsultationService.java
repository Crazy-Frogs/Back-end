// ConsultationService.java
package sesi.petvita.consultation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.consultation.dto.ConsultationRequestDTO;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.mapper.ConsultationMapper;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository repository;
    private final ConsultationMapper mapper;

    public ConsultationResponseDTO save(ConsultationRequestDTO dto) {
        ConsultationModel model = mapper.toModel(dto);
        return mapper.toDTO(repository.save(model));
    }

    public List<ConsultationResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public ConsultationResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow();
    }

    public ConsultationResponseDTO update(Long id, ConsultationRequestDTO dto) {
        ConsultationModel existing = repository.findById(id).orElseThrow();
        ConsultationModel updated = mapper.toModel(dto);
        updated.setId(existing.getId());
        return mapper.toDTO(repository.save(updated));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
