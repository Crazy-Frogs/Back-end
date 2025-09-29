package sesi.petvita.user.model;


import com.fasterxml.jackson.annotation.JsonIgnore; // Importe esta anotação!
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.user.role.UserRole;


import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).+$", message = "A senha deve conter pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.")
    @Column(nullable = false)
    private String password;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(unique = true , nullable = false)
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\d{2}\\d{8,9}$", message = "Formato de telefone inválido (ex: 11987654321)")
    @Column(unique = true , nullable = false)
    private String phone;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\d{7,9}X?$", message = "Formato de RG inválido")
    @Column(unique = true , nullable = false)
    private String rg;

    @NotBlank
    @Column(nullable = false)
    private String imageurl;
    private String imagePublicId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @JsonManagedReference
    @OneToMany(mappedBy = "usuario" ,cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<ConsultationModel> consultas;

    @JsonManagedReference
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetModel> pets;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}