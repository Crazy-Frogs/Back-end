package sesi.petvita.veterinary.dto;

import lombok.*;
import sesi.petvita.veterinary.model.VeterinaryModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinaryResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String password;
    private String crmv;
    private String especialityenum;
    private String phone;

    public static VeterinaryResponseDTO fromModel(VeterinaryModel veterinaryModel){
        return VeterinaryResponseDTO.builder()
                .id(veterinaryModel.getId())
                .name(veterinaryModel.getName())
                .email(veterinaryModel.getEmail())
                .phone(veterinaryModel.getPhone())
                .especialityenum(veterinaryModel.getEspecialityenum().getDescricao()) // obter a descrição do Enum
                .build();
    }
}
