package sesi.petvita.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import sesi.petvita.clinic.careservices.CareServices;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "clinics")
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

    @NotBlank
    @Enumerated(EnumType.STRING)
    private CareServices careServices;
}
