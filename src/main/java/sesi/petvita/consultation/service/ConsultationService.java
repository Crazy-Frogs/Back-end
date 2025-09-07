package sesi.petvita.consultation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesi.petvita.consultation.dto.ConsultationRequestDTO;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.dto.ConsultationUpdateRequestDTO;
import sesi.petvita.consultation.mapper.ConsultationMapper;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.consultation.status.ConsultationStatus;
import sesi.petvita.notification.service.NotificationService;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.repository.VeterinaryRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
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
    private final ConsultationMapper consultationMapper;
    private final NotificationService notificationService;

    @Transactional
    public ConsultationResponseDTO create(ConsultationRequestDTO dto, UserModel user) {
        if (consultationRepository.existsByVeterinarioIdAndConsultationdateAndConsultationtime(dto.veterinarioId(), dto.consultationdate(), dto.consultationtime())) {
            throw new IllegalStateException("Conflito de horário. O veterinário já possui uma consulta neste horário.");
        }

        PetModel pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new NoSuchElementException("Pet não encontrado com o ID: " + dto.petId()));

        VeterinaryModel vet = veterinaryRepository.findById(dto.veterinarioId())
                .orElseThrow(() -> new NoSuchElementException("Veterinário não encontrado com o ID: " + dto.veterinarioId()));

        ConsultationModel newConsultation = ConsultationModel.builder()
                .consultationdate(dto.consultationdate())
                .consultationtime(dto.consultationtime())
                .specialityEnum(dto.specialityEnum())
                .status(ConsultationStatus.PENDENTE)
                .reason(dto.reason())
                .observations(dto.observations())
                .usuario(user)
                .pet(pet)
                .veterinario(vet)
                .build();

        ConsultationModel savedConsultation = consultationRepository.save(newConsultation);
        return consultationMapper.toDTO(savedConsultation);
    }

    @Transactional
    public void acceptConsultation(Long consultationId) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (consultation.getStatus() != ConsultationStatus.PENDENTE) {
            throw new IllegalStateException("Apenas consultas com status 'PENDENTE' podem ser aceitas.");
        }
        consultation.setStatus(ConsultationStatus.AGENDADA);
        consultationRepository.save(consultation);
        notificationService.createNotification(consultation.getUsuario(), "Sua consulta para " + consultation.getPet().getName() + " foi agendada!");
    }

    @Transactional
    public void rejectConsultation(Long consultationId) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (consultation.getStatus() != ConsultationStatus.PENDENTE) {
            throw new IllegalStateException("Apenas consultas com status 'PENDENTE' podem ser recusadas.");
        }
        consultation.setStatus(ConsultationStatus.RECUSADA);
        consultationRepository.save(consultation);
        notificationService.createNotification(consultation.getUsuario(), "Sua solicitação de consulta para " + consultation.getPet().getName() + " foi recusada.");
    }

    @Transactional
    public void cancelConsultation(Long consultationId, UserModel user) { // Adicione UserModel user como parâmetro
        ConsultationModel consultation = findByIdOrThrow(consultationId);

        // VERIFICAÇÃO DE PROPRIEDADE
        if (!consultation.getUsuario().getId().equals(user.getId())) {
            throw new IllegalStateException("Você só pode cancelar suas próprias consultas.");
        }

        if (consultation.getStatus() != ConsultationStatus.AGENDADA) {
            throw new IllegalStateException("Apenas consultas 'AGENDADAS' podem ser canceladas.");
        }
        consultation.setStatus(ConsultationStatus.CANCELADA);
        consultationRepository.save(consultation);

        // Notificar o veterinário
        notificationService.createNotification(consultation.getVeterinario().getUserAccount(),
                "A consulta para " + consultation.getPet().getName() + " foi cancelada pelo cliente.");
    }

    @Transactional
    public void finalizeConsultation(Long consultationId) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (consultation.getStatus() != ConsultationStatus.AGENDADA) {
            throw new IllegalStateException("Apenas consultas 'AGENDADAS' podem ser finalizadas.");
        }
        consultation.setStatus(ConsultationStatus.FINALIZADA);
        consultationRepository.save(consultation);
        notificationService.createNotification(consultation.getUsuario(), "Sua consulta para " + consultation.getPet().getName() + " foi finalizada. O relatório estará disponível em breve.");
    }

    @Transactional
    public ConsultationResponseDTO updateConsultation(Long consultationId, ConsultationUpdateRequestDTO dto, UserModel user) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);

        if (!consultation.getUsuario().getId().equals(user.getId())) {
            throw new IllegalStateException("Você só pode editar suas próprias consultas.");
        }

        if (dto.consultationdate() != null) consultation.setConsultationdate(dto.consultationdate());
        if (dto.consultationtime() != null) consultation.setConsultationtime(dto.consultationtime());
        if (dto.reason() != null) consultation.setReason(dto.reason());
        if (dto.observations() != null) consultation.setObservations(dto.observations());

        ConsultationModel updatedConsultation = consultationRepository.save(consultation);
        return consultationMapper.toDTO(updatedConsultation);
    }

    @Transactional
    public void writeReport(Long consultationId, String report) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (consultation.getStatus() != ConsultationStatus.FINALIZADA) {
            throw new IllegalStateException("O relatório só pode ser preenchido para consultas 'FINALIZADAS'.");
        }
        consultation.setDoctorReport(report);
        consultationRepository.save(consultation);
        notificationService.createNotification(consultation.getUsuario(), "O relatório da sua consulta para " + consultation.getPet().getName() + " está disponível para visualização.");
    }

    public List<ConsultationResponseDTO> findAllForAdmin() {
        return consultationRepository.findAll().stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findForAuthenticatedUser(UserModel user) {
        return consultationRepository.findByUsuarioId(user.getId()).stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    public ConsultationResponseDTO findById(Long id) {
        return consultationRepository.findById(id).map(consultationMapper::toDTO).orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + id));
    }

    public List<ConsultationResponseDTO> findConsultationsByDate(LocalDate date) {
        return consultationRepository.findByConsultationdate(date).stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsBySpeciality(SpecialityEnum speciality) {
        return consultationRepository.findBySpecialityEnum(speciality).stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsByVeterinaryName(String veterinaryName) {
        return consultationRepository.findByVeterinario_NameContainingIgnoreCase(veterinaryName).stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    public List<ConsultationResponseDTO> findConsultationsByPetName(String petName) {
        return consultationRepository.findByPet_NameContainingIgnoreCase(petName).stream().map(consultationMapper::toDTO).collect(Collectors.toList());
    }

    private ConsultationModel findByIdOrThrow(Long id) {
        return consultationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + id));
    }

    public List<ConsultationResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return consultationRepository.findByConsultationdateBetween(startDate, endDate)
                .stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }
}