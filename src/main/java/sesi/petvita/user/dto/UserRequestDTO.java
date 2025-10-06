package sesi.petvita.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,

        @Email @NotBlank @Size(max = 100)
        String email,

        @NotBlank
        String phone,

        @NotBlank @Size(max = 200)
        String address,

        @NotBlank
        String rg,

        @NotBlank
        String imageurl
) {}
