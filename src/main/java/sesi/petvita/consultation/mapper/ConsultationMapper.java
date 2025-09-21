package sesi.petvita.consultation.mapper;


import org.springframework.stereotype.Component;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.model.ConsultationModel;

@Component
public class ConsultationMapper {

    public ConsultationResponseDTO toDTO(ConsultationModel model) {
        return new ConsultationResponseDTO(
                model.getId(),
                model.getConsultationdate(),
                model.getConsultationtime(),
                model.getSpecialityEnum().getDescricao(),
                model.getStatus(),
                model.getReason(),
                model.getObservations(),
                model.getPet() != null ? model.getPet().getName() : "N/A", // Pega o nome do pet
                model.getVeterinario() != null ? model.getVeterinario().getName() : "N/A", // Pega o nome do vet
                model.getUsuario() != null ? model.getUsuario().getId() : null,
                model.getUsuario() != null ? model.getUsuario().getUsername() : "N/A"
        );
    }
}