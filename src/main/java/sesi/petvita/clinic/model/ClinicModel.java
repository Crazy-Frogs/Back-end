package sesi.petvita.clinic.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import sesi.petvita.clinic.careservices.CareServices;
import sesi.petvita.consultation.model.ConsultationModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "clinicas")
public class ClinicModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\d{2}\\d{8,9}$", message = "Formato de telefone inválido (ex: 11987654321)")
    @Column(unique = true)
    private String phone;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CareServices careServices;

    @NotBlank
    private String imageurl;

    @JsonManagedReference
    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsultationModel> consultas;


}

