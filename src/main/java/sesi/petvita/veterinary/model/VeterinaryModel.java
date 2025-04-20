package sesi.petvita.veterinary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import sesi.petvita.veterinary.especiality.EspecialityEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "veterinary")
public class VeterinaryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3 , max = 50)
    private String name;

    @NotBlank
    @Size(min = 3 , max = 30)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).+$", message = "A senha deve conter pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.")
    private String password;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @Size(min = 6, max = 15, message = "O CRM deve ter entre 6 e 15 caracteres.")
    @Pattern(regexp = "^[A-Za-z]{2}\\s?\\d+$", message = "Formato de CRM inválido. Ex: SP 123456 ou SP123456")
    private String crmv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialityEnum especialityenum;

    @NotBlank
    @Pattern(regexp = "^\\d{2}\\d{8,9}$", message = "Formato de telefone inválido (ex: 11987654321)")
    @Column(unique = true)
    private String phone;

}
