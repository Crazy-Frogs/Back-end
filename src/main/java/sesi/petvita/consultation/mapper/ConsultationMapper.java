// ConsultationMapper.java
package sesi.petvita.consultation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sesi.petvita.clinic.repository.ClinicRepository;
import sesi.petvita.consultation.dto.ConsultationRequestDTO;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.veterinary.repository.VeterinaryRepository;

@Component
@RequiredArgsConstructor
public class ConsultationMapper {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final ClinicRepository clinicRepository;

    public ConsultationModel toModel(ConsultationRequestDTO dto) {
        return ConsultationModel.builder()
                .consultationdate(dto.consultationdate())
                .consultationtime(dto.consultationtime())
                .specialityEnum(dto.specialityEnum())
                .status(dto.status())
                .reason(dto.reason())
                .observations(dto.observations())
                .pet(petRepository.findById(dto.petId()).orElseThrow())
                .usuario(userRepository.findById(dto.usuarioId()).orElseThrow())
                .veterinario(veterinaryRepository.findById(dto.veterinarioId()).orElseThrow())
                .clinica(clinicRepository.findById(dto.clinicaId()).orElseThrow())
                .build();
    }

    public ConsultationResponseDTO toDTO(ConsultationModel model) {
        return new ConsultationResponseDTO(
                model.getId(),
                model.getConsultationdate(),
                model.getConsultationtime(),
                model.getSpecialityEnum(),
                model.getStatus(),
                model.getReason(),
                model.getObservations(),
                model.getPet().getId(),
                model.getUsuario().getId(),
                model.getVeterinario().getId(),
                model.getClinica().getId(),
                model.getDataCriacao(),
                model.getDataAtualizacao()
        );
    }
}
