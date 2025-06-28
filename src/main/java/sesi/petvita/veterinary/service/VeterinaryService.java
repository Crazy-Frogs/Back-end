package sesi.petvita.veterinary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.consultation.status.ConsultationStatus;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.user.role.UserRole;
import sesi.petvita.veterinary.dto.VeterinarianMonthlyReportDTO;
import sesi.petvita.veterinary.dto.VeterinaryRatingRequestDTO;
import sesi.petvita.veterinary.dto.VeterinaryRequestDTO;
import sesi.petvita.veterinary.dto.VeterinaryResponseDTO;
import sesi.petvita.veterinary.mapper.VeterinaryMapper;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.model.VeterinaryRating;
import sesi.petvita.veterinary.repository.VeterinaryRatingRepository;
import sesi.petvita.veterinary.repository.VeterinaryRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeterinaryService {

    private final VeterinaryRepository veterinaryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VeterinaryMapper veterinaryMapper;
    private final VeterinaryRatingRepository ratingRepository;
    private final ConsultationRepository consultationRepository;

    @Transactional
    public VeterinaryResponseDTO createVeterinary(VeterinaryRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalStateException("Este e-mail já está em uso por outro usuário.");
        }

        UserModel userAccount = UserModel.builder()
                .username(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .phone(dto.phone())
                .role(UserRole.VETERINARY)
                .address("Não informado")
                .rg("Não informado")
                .imageurl(dto.imageurl())
                .build();
        UserModel savedUserAccount = userRepository.save(userAccount);

        VeterinaryModel newVeterinary = VeterinaryModel.builder()
                .name(dto.name())
                .email(dto.email())
                .crmv(dto.crmv())
                .specialityenum(dto.specialityenum())
                .phone(dto.phone())
                .imageurl(dto.imageurl())
                .userAccount(savedUserAccount)
                .build();
        VeterinaryModel savedVeterinary = veterinaryRepository.save(newVeterinary);
        return veterinaryMapper.toDTO(savedVeterinary);
    }

    @Transactional
    public VeterinaryResponseDTO updateVeterinary(Long id, VeterinaryRequestDTO dto) {
        VeterinaryModel vet = veterinaryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + id));

        UserModel userAccount = vet.getUserAccount();
        if (userAccount == null) {
            throw new IllegalStateException("Perfil de veterinário sem conta de usuário associada.");
        }

        userAccount.setUsername(dto.name());
        userAccount.setEmail(dto.email());
        userAccount.setPhone(dto.phone());
        userAccount.setImageurl(dto.imageurl());
        if (dto.password() != null && !dto.password().isEmpty()) {
            userAccount.setPassword(passwordEncoder.encode(dto.password()));
        }
        userRepository.save(userAccount);

        vet.setName(dto.name());
        vet.setEmail(dto.email());
        vet.setCrmv(dto.crmv());
        vet.setSpecialityenum(dto.specialityenum());
        vet.setPhone(dto.phone());
        vet.setImageurl(dto.imageurl());
        VeterinaryModel updatedVet = veterinaryRepository.save(vet);

        return veterinaryMapper.toDTO(updatedVet);
    }

    @Transactional
    public void deleteVeterinary(Long id) {
        if (!veterinaryRepository.existsById(id)) {
            throw new NoSuchElementException("Veterinário não encontrado com o ID: " + id);
        }
        // A lógica para deletar o usuário associado pode ser adicionada aqui se necessário
        veterinaryRepository.deleteById(id);
    }

    @Transactional
    public void addRating(Long veterinaryId, Long userId, VeterinaryRatingRequestDTO dto) {
        VeterinaryModel vet = veterinaryRepository.findById(veterinaryId)
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado."));
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));

        VeterinaryRating newRating = VeterinaryRating.builder()
                .veterinary(vet)
                .user(user)
                .rating(dto.rating())
                .comment(dto.comment())
                .build();
        ratingRepository.save(newRating);

        int newRatingCount = vet.getRatingCount() + 1;
        double newAverage = vet.getAverageRating() + (dto.rating() - vet.getAverageRating()) / newRatingCount;

        vet.setRatingCount(newRatingCount);
        vet.setAverageRating(newAverage);
        veterinaryRepository.save(vet);
    }

    public List<VeterinaryResponseDTO> findAll() {
        return veterinaryRepository.findAll().stream()
                .map(veterinaryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VeterinaryResponseDTO findById(Long id) {
        return veterinaryRepository.findById(id)
                .map(veterinaryMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + id));
    }

    public List<VeterinaryResponseDTO> searchVeterinarians(String name, SpecialityEnum speciality) {
        List<VeterinaryModel> result;
        if (name != null && speciality != null) {
            result = veterinaryRepository.findByNameContainingIgnoreCaseAndSpecialityenum(name, speciality);
        } else if (name != null) {
            result = veterinaryRepository.findByNameContainingIgnoreCase(name);
        } else if (speciality != null) {
            result = veterinaryRepository.findBySpecialityenum(speciality);
        } else {
            result = veterinaryRepository.findAll();
        }
        return result.stream().map(veterinaryMapper::toDTO).collect(Collectors.toList());
    }

    public VeterinarianMonthlyReportDTO getMonthlyReport(UserModel user) {
        VeterinaryModel vet = veterinaryRepository.findByUserAccount(user)
                .orElseThrow(() -> new IllegalStateException("Perfil de veterinário não encontrado para este usuário."));

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<ConsultationModel> monthlyConsultations = consultationRepository.findAll().stream()
                .filter(c -> c.getVeterinario().getId().equals(vet.getId()) &&
                        c.getConsultationdate().getYear() == year &&
                        c.getConsultationdate().getMonthValue() == month)
                .collect(Collectors.toList());

        long total = monthlyConsultations.size();
        long finalized = monthlyConsultations.stream().filter(c -> c.getStatus() == ConsultationStatus.FINALIZADA).count();
        long pending = monthlyConsultations.stream().filter(c -> c.getStatus() == ConsultationStatus.PENDENTE).count();
        Set<String> patients = monthlyConsultations.stream().map(c -> c.getPet().getName()).collect(Collectors.toSet());

        return new VeterinarianMonthlyReportDTO(year, month, total, finalized, pending, patients);
    }
}