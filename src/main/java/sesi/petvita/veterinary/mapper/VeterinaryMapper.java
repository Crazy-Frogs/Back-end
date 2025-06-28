package sesi.petvita.veterinary.mapper;


import org.springframework.stereotype.Component;
import sesi.petvita.veterinary.dto.VeterinaryRequestDTO;
import sesi.petvita.veterinary.dto.VeterinaryResponseDTO;
import sesi.petvita.veterinary.model.VeterinaryModel;

@Component
public class VeterinaryMapper {

    public VeterinaryModel toModel(VeterinaryRequestDTO requestDTO) {
        return VeterinaryModel.builder()
                .name(requestDTO.name())
                .email(requestDTO.email())
                .password(requestDTO.password()) // A senha será codificada no controller
                .crmv(requestDTO.crmv())
                .specialityenum(requestDTO.specialityenum())
                .phone(requestDTO.phone())
                .imageurl(requestDTO.imageurl())
                .build();
    }

    public VeterinaryResponseDTO toDTO(VeterinaryModel model) {
        return new VeterinaryResponseDTO(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getCrmv(),
                model.getSpecialityenum(),
                model.getPhone(),
                model.getImageurl(),
                model.getAverageRating(),
                model.getRatingCount()
        );
    }
}
