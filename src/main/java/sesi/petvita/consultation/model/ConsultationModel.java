package sesi.petvita.consultation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import sesi.petvita.clinic.model.ClinicModel;
import sesi.petvita.consultation.status.ConsultationStatus;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.speciality.SpecialityEnum;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        @NotBlank
        private String imageurl;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @Column(nullable = false)
        private LocalDate consultationdate;

        @JsonFormat(pattern = "HH:mm")
        @Column(nullable = false)
        private LocalTime consultationtime;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private SpecialityEnum specialityEnum;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ConsultationStatus status;

        @Column(nullable = false)
        private String reason;

        @Column(nullable = false)
        private String observations;

        @ManyToOne
        @JoinColumn(name = "pet_id")
        private PetModel pet;

        @ManyToOne
        @JoinColumn(name = "usuario_id")
        private UserModel usuario;

        @ManyToOne
        @JoinColumn(name = "veterinario_id")
        private VeterinaryModel veterinario;

        @ManyToOne
        @JoinColumn(name = "clinica_id")
        private ClinicModel clinica;

        private LocalDateTime dataCriacao;

        private LocalDateTime dataAtualizacao;

        @PrePersist
        public void prePersist() {
            this.dataCriacao = LocalDateTime.now();
        }

        @PreUpdate
        public void preUpdate() {
            this.dataAtualizacao = LocalDateTime.now();
        }


    }

