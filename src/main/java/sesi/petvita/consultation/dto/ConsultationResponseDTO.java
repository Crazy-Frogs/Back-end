package sesi.petvita.consultation.dto;


import sesi.petvita.consultation.status.ConsultationStatus;

import java.time.LocalDate;
import java.time.LocalTime;


public record ConsultationResponseDTO(
        Long id,
        LocalDate consultationdate,
        LocalTime consultationtime,
        String speciality,
        ConsultationStatus status,
        String reason,
        String observations,
        String petName,
        String veterinaryName,
        Long usuarioId
) {}