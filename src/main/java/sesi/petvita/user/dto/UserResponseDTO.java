package sesi.petvita.user.dto;


import sesi.petvita.user.role.UserRole;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String phone,
        UserRole role
) {}