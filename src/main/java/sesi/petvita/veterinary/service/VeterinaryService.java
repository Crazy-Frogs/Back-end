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
import java.time.LocalTime;
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

        String encodedPassword = passwordEncoder.encode(dto.password());

        UserModel userAccount = UserModel.builder()
                .username(dto.name())
                .email(dto.email())
                .password(encodedPassword)
                .phone(dto.phone())
                .role(UserRole.VETERINARY)
                .address("Não informado")
                .rg(dto.rg())
                .imageurl(dto.imageurl())
                .build();
        UserModel savedUserAccount = userRepository.save(userAccount);

        VeterinaryModel newVeterinary = VeterinaryModel.builder()
                .name(dto.name())
                .email(dto.email())
                .password(encodedPassword) // <-- CORREÇÃO: Esta linha estava faltando
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
        userAccount.setRg(dto.rg());

        if (dto.password() != null && !dto.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(dto.password());
            userAccount.setPassword(encodedPassword);
            vet.setPassword(encodedPassword); // Garante que a senha seja atualizada nos dois lugares
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
        VeterinaryModel vet = veterinaryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + id));

        if (vet.getUserAccount() != null) {
            userRepository.delete(vet.getUserAccount());
        }
        veterinaryRepository.delete(vet);
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

        // Recalcula a média de forma mais robusta
        List<VeterinaryRating> allRatings = vet.getRatings();
        double totalRating = allRatings.stream().mapToDouble(VeterinaryRating::getRating).sum();
        vet.setRatingCount(allRatings.size());
        vet.setAverageRating(totalRating / allRatings.size());

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

    public VeterinaryResponseDTO findVeterinaryByUserAccount(UserModel user) {
        return veterinaryRepository.findByUserAccount(user)
                .map(veterinaryMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Perfil de veterinário não encontrado para este usuário."));
    }

    public List<VeterinaryResponseDTO> searchVeterinarians(String name, SpecialityEnum speciality) {
        List<VeterinaryModel> result;
        if (name != null && !name.isEmpty() && speciality != null) {
            result = veterinaryRepository.findByNameContainingIgnoreCaseAndSpecialityenum(name, speciality);
        } else if (name != null && !name.isEmpty()) {
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
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<ConsultationModel> monthlyConsultations = consultationRepository.findByVeterinarioAndConsultationdateBetween(vet, startDate, endDate);

        long total = monthlyConsultations.size();
        long finalized = monthlyConsultations.stream().filter(c -> c.getStatus() == ConsultationStatus.FINALIZADA).count();
        long pending = monthlyConsultations.stream().filter(c -> c.getStatus() == ConsultationStatus.PENDENTE).count();
        Set<String> patients = monthlyConsultations.stream().map(c -> c.getPet().getName()).collect(Collectors.toSet());

        return new VeterinarianMonthlyReportDTO(year, month, total, finalized, pending, patients);
    }

    public List<LocalTime> getAvailableSlots(Long vetId, LocalDate date) {
        List<LocalTime> allDaySlots = List.of(
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)
        );

        List<LocalTime> bookedSlots = consultationRepository.findByVeterinarioId(vetId).stream()
                .filter(c -> c.getConsultationdate().equals(date) &&
                        (c.getStatus() == ConsultationStatus.AGENDADA || c.getStatus() == ConsultationStatus.PENDENTE))
                .map(ConsultationModel::getConsultationtime)
                .collect(Collectors.toList());

        return allDaySlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }
}