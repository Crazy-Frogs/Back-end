package sesi.petvita.clinic.dto;


import sesi.petvita.clinic.careservices.CareServices;

public record ClinicResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        CareServices careServices,
        String imageurl
) {
}
