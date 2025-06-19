package sesi.petvita.clinic.mapper;


import org.springframework.stereotype.Component;
import sesi.petvita.clinic.dto.ClinicRequestDTO;
import sesi.petvita.clinic.dto.ClinicResponseDTO;
import sesi.petvita.clinic.model.ClinicModel;

@Component
public class ClinicMapper {


    public ClinicModel toModel(ClinicRequestDTO requestDTO) {
        return ClinicModel.builder()
                .name(requestDTO.name())
                .email(requestDTO.email())
                .phone(requestDTO.phone())
                .address(requestDTO.address())
                .careServices(requestDTO.careServices())
                .imageurl(requestDTO.imageurl())
                .build();
    }

    public ClinicResponseDTO toDTO(ClinicModel clinicModel) {
        return new ClinicResponseDTO(
                clinicModel.getId(),
                clinicModel.getName(),
                clinicModel.getEmail(),
                clinicModel.getPhone(),
                clinicModel.getAddress(),
                clinicModel.getCareServices(),
                clinicModel.getImageurl()
        );
    }
}