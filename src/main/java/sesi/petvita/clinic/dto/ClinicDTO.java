package sesi.petvita.clinic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sesi.petvita.clinic.careservices.CareServices;

@Schema(description = "Sistema de Clinicas")
public record ClinicDTO(
        @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        Long id,

        @Schema(description = "Nome de usuário", example = "clinica do joaozinho")
        String name,

        @Schema(description = "Email do usuário", example = "joao@email.com")
        String email,

        @Schema(description = "Telefone", example = "11999999999")
        String phone,

        @Schema(description = "Endereço completo", example = "Rua Exemplo, 123 - São Paulo")
        String address,

       @Schema(description = "Endereço completo", example = "Rua Exemplo, 123 - São Paulo")
        CareServices careServices
) {


}
