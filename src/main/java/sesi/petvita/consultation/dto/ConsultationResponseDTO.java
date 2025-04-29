// ConsultationResponseDTO.java
package sesi.petvita.consultation.dto;

import sesi.petvita.consultation.status.ConsultationStatus;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record ConsultationResponseDTO(
        Long id,
        LocalDate consultationdate,
        LocalTime consultationtime,
        SpecialityEnum specialityEnum,
        ConsultationStatus status,
        String reason,
        String observations,
        Long petId,
        Long usuarioId,
        Long veterinarioId,
        Long clinicaId,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {}
