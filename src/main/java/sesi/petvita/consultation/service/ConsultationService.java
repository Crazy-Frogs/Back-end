package sesi.petvita.consultation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // Importe este
import org.springframework.stereotype.Service;
import sesi.petvita.clinic.model.ClinicModel;
import sesi.petvita.clinic.repository.ClinicRepository;
import sesi.petvita.consultation.dto.ConsultationRequestDTO;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.mapper.ConsultationMapper;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.repository.VeterinaryRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {


    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final ClinicRepository clinicRepository;
    private final ConsultationMapper consultationMapper;


    public List<ConsultationResponseDTO> findAllForAdmin() {
        return consultationRepository.findAll().stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findForAuthenticatedUser(Authentication authentication) {
        UserModel loggedInUser = (UserModel) authentication.getPrincipal();

        return consultationRepository.findByUsuarioId(loggedInUser.getId()).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ConsultationResponseDTO save(ConsultationRequestDTO dto) {
        LocalTime newAppointmentTime = dto.consultationtime();
        LocalTime startTime = newAppointmentTime.minusMinutes(39);
        LocalTime endTime = newAppointmentTime.plusMinutes(39);

        List<ConsultationModel> conflictingAppointments = consultationRepository
                .findByVeterinarioIdAndConsultationdateAndConsultationtimeBetween(
                        dto.veterinarioId(),
                        dto.consultationdate(),
                        startTime,
                        endTime
                );

        if (!conflictingAppointments.isEmpty()) {
            throw new IllegalStateException("Conflito de horário. Já existe uma consulta agendada a menos de 40 minutos deste horário para o mesmo veterinário.");
        }

        UserModel user = userRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + dto.usuarioId()));
        PetModel pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new NoSuchElementException("Pet não encontrado com o ID: " + dto.petId()));
        VeterinaryModel vet = veterinaryRepository.findById(dto.veterinarioId())
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + dto.veterinarioId()));
        ClinicModel clinic = clinicRepository.findById(dto.clinicaId())
                .orElseThrow(() -> new NoSuchElementException("Clínica não encontrada com o ID: " + dto.clinicaId()));

        // 3. CRIAÇÃO DO MODELO
        ConsultationModel newConsultation = ConsultationModel.builder()
                .consultationdate(dto.consultationdate())
                .consultationtime(dto.consultationtime())
                .specialityEnum(dto.specialityEnum())
                .status(dto.status())
                .reason(dto.reason())
                .observations(dto.observations())
                .usuario(user)
                .pet(pet)
                .veterinario(vet)
                .clinica(clinic)
                .build();

        ConsultationModel savedConsultation = consultationRepository.save(newConsultation);
        return consultationMapper.toDTO(savedConsultation);
    }

    public ConsultationResponseDTO update(Long id, ConsultationRequestDTO dto) {
        ConsultationModel existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada para atualização com o ID: " + id));

        UserModel user = userRepository.findById(dto.usuarioId()).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));
        PetModel pet = petRepository.findById(dto.petId()).orElseThrow(() -> new NoSuchElementException("Pet não encontrado."));
        VeterinaryModel vet = veterinaryRepository.findById(dto.veterinarioId()).orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado."));
        ClinicModel clinic = clinicRepository.findById(dto.clinicaId()).orElseThrow(() -> new NoSuchElementException("Clínica não encontrada."));

        existingConsultation.setConsultationdate(dto.consultationdate());
        existingConsultation.setConsultationtime(dto.consultationtime());
        existingConsultation.setSpecialityEnum(dto.specialityEnum());
        existingConsultation.setStatus(dto.status());
        existingConsultation.setReason(dto.reason());
        existingConsultation.setObservations(dto.observations());
        existingConsultation.setUsuario(user);
        existingConsultation.setPet(pet);
        existingConsultation.setVeterinario(vet);
        existingConsultation.setClinica(clinic);

        ConsultationModel updatedConsultation = consultationRepository.save(existingConsultation);
        return consultationMapper.toDTO(updatedConsultation);
    }

    public void delete(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new NoSuchElementException("Consulta não encontrada para exclusão com o ID: " + id);
        }
        consultationRepository.deleteById(id);
    }

    public List<ConsultationResponseDTO> findAll() {
        return consultationRepository.findAll().stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ConsultationResponseDTO findById(Long id) {
        return consultationRepository.findById(id)
                .map(consultationMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + id));
    }

    public List<ConsultationResponseDTO> findConsultationsByDate(LocalDate date) {
        return consultationRepository.findByConsultationdate(date).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsBySpeciality(SpecialityEnum speciality) {
        return consultationRepository.findBySpecialityEnum(speciality).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsByVeterinaryName(String veterinaryName) {
        return consultationRepository.findByVeterinario_NameContainingIgnoreCase(veterinaryName).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsByPetName(String petName) {
        return consultationRepository.findByPet_NameContainingIgnoreCase(petName).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }
}