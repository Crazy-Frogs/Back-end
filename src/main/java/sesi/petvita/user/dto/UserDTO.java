package sesi.petvita.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Representa um usuário na resposta")
public record UserDTO(
        @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        Long id,

        @Schema(description = "Nome de usuário", example = "joaosilva")
        String username,

        @Schema(description = "Senha do usuário", example = "123456")
        String password,

        @Schema(description = "Telefone", example = "11999999999")
        String phone,

        @Schema(description = "Email do usuário", example = "joao@email.com")
        String email,

        @Schema(description = "Endereço completo", example = "Rua Exemplo, 123 - São Paulo")
        String address,

        @Schema(description = "RG do usuário", example = "123456789")
        String rg
) {}
