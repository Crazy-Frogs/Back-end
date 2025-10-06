package sesi.petvita.veterinary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

public record VeterinaryRequestDTO(
        @NotBlank @Size(min = 3, max = 50)
        String name,


        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String password,


        @Email @NotBlank @Size(max = 100)
        String email,

        @NotBlank @Size(min = 6, max = 15)
        @Pattern(regexp = "^[A-Za-z]{2}\\s?\\d+$", message = "Formato de CRMV inválido")
        String crmv,

        @NotBlank
        @Size(min = 7, max = 14, message = "O RG deve ter entre 7 e 14 caracteres.")
        String rg,

        SpecialityEnum specialityenum,

        @NotBlank
        @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres.")
        String phone,

        @NotBlank
        String imageurl
) {}