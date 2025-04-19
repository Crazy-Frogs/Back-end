package sesi.petvita.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 8) // Exemplo de requisito mínimo para senha (a ser hasheada!)
    private String password;

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
    @Pattern(regexp = "^\\d{7,9}X?$", message = "Formato de RG inválido")
    @Column(unique = true)
    private String rg;
}
