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
import sesi.petvita.notification.service.EmailService;
import sesi.petvita.notification.service.NotificationService;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.user.role.UserRole;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.repository.VeterinaryRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService; // Serviço de E-mail injetado

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

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

        // Envio de e-mail para o VETERINÁRIO sobre a nova solicitação
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("titulo", "Nova Solicitação de Consulta");
        emailModel.put("nomeUsuario", vet.getName());
        emailModel.put("corpoMensagem", "Você recebeu uma nova solicitação de consulta de " + user.getUsername() + ". Por favor, acesse o painel para aceitar ou recusar.");
        emailModel.put("mostrarDetalhesConsulta", true);
        emailModel.put("nomePet", pet.getName());
        emailModel.put("nomeVeterinario", vet.getName());
        emailModel.put("dataConsulta", savedConsultation.getConsultationdate().format(dateFormatter));
        emailModel.put("horarioConsulta", savedConsultation.getConsultationtime().format(timeFormatter));
        emailService.sendHtmlEmailFromTemplate(vet.getEmail(), "Nova Solicitação de Consulta - Pet Vita", emailModel);

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

        // Envio de e-mail para o CLIENTE
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("titulo", "Consulta Confirmada!");
        emailModel.put("nomeUsuario", consultation.getUsuario().getUsername());
        emailModel.put("corpoMensagem", "Sua solicitação de consulta foi aceita pelo(a) Dr(a). " + consultation.getVeterinario().getName() + ". Estamos ansiosos para ver você e seu pet!");
        emailModel.put("mostrarDetalhesConsulta", true);
        emailModel.put("nomePet", consultation.getPet().getName());
        emailModel.put("nomeVeterinario", consultation.getVeterinario().getName());
        emailModel.put("dataConsulta", consultation.getConsultationdate().format(dateFormatter));
        emailModel.put("horarioConsulta", consultation.getConsultationtime().format(timeFormatter));
        emailService.sendHtmlEmailFromTemplate(consultation.getUsuario().getEmail(), "Sua Consulta foi Confirmada - Pet Vita", emailModel);
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

        // Envio de e-mail para o CLIENTE
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("titulo", "Solicitação de Consulta Recusada");
        emailModel.put("nomeUsuario", consultation.getUsuario().getUsername());
        emailModel.put("corpoMensagem", "Infelizmente, sua solicitação de consulta para o pet " + consultation.getPet().getName() + " não pôde ser aceita no momento. Por favor, tente agendar um novo horário.");
        emailModel.put("mostrarDetalhesConsulta", false);
        emailService.sendHtmlEmailFromTemplate(consultation.getUsuario().getEmail(), "Solicitação de Consulta Recusada - Pet Vita", emailModel);
    }

    @Transactional
    public void cancelConsultation(Long consultationId, UserModel user) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (consultation.getStatus() != ConsultationStatus.AGENDADA) {
            throw new IllegalStateException("Apenas consultas 'AGENDADAS' podem ser canceladas.");
        }

        UserModel targetUserToNotify;
        String recipientEmail;
        String recipientName;
        String emailTitle;
        String emailBody;

        // Verifica quem está cancelando para notificar a outra parte
        if (user.getRole() == UserRole.USER) {
            if (!consultation.getUsuario().getId().equals(user.getId())) {
                throw new IllegalStateException("Você só pode cancelar suas próprias consultas.");
            }
            targetUserToNotify = consultation.getVeterinario().getUserAccount();
            recipientEmail = consultation.getVeterinario().getEmail();
            recipientName = consultation.getVeterinario().getName();
            emailTitle = "Consulta Cancelada pelo Cliente";
            emailBody = "Uma consulta agendada com você foi cancelada pelo cliente. Por favor, verifique sua agenda.";
        } else if (user.getRole() == UserRole.VETERINARY) {
            if (!consultation.getVeterinario().getUserAccount().getId().equals(user.getId())) {
                throw new IllegalStateException("Você só pode cancelar suas próprias consultas.");
            }
            targetUserToNotify = consultation.getUsuario();
            recipientEmail = consultation.getUsuario().getEmail();
            recipientName = consultation.getUsuario().getUsername();
            emailTitle = "Consulta Cancelada pelo Veterinário";
            emailBody = "Sua consulta foi cancelada pelo veterinário. Por favor, entre em contato ou agende um novo horário.";
        } else {
            throw new IllegalStateException("Ação de cancelamento não permitida para este usuário.");
        }

        consultation.setStatus(ConsultationStatus.CANCELADA);
        consultationRepository.save(consultation);

        notificationService.createNotification(targetUserToNotify, "A consulta para " + consultation.getPet().getName() + " foi cancelada.");

        // Envio de e-mail para a outra parte
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("titulo", emailTitle);
        emailModel.put("nomeUsuario", recipientName);
        emailModel.put("corpoMensagem", emailBody);
        emailModel.put("mostrarDetalhesConsulta", true);
        emailModel.put("nomePet", consultation.getPet().getName());
        emailModel.put("nomeVeterinario", consultation.getVeterinario().getName());
        emailModel.put("dataConsulta", consultation.getConsultationdate().format(dateFormatter));
        emailModel.put("horarioConsulta", consultation.getConsultationtime().format(timeFormatter));
        emailService.sendHtmlEmailFromTemplate(recipientEmail, emailTitle + " - Pet Vita", emailModel);
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

        // Envio de e-mail para o VETERINÁRIO notificando da alteração
        Map<String, Object> emailModel = new HashMap<>();
        emailModel.put("titulo", "Consulta Alterada pelo Cliente");
        emailModel.put("nomeUsuario", updatedConsultation.getVeterinario().getName());
        emailModel.put("corpoMensagem", "Os detalhes de uma consulta agendada com você foram alterados pelo cliente. Verifique as novas informações:");
        emailModel.put("mostrarDetalhesConsulta", true);
        emailModel.put("nomePet", updatedConsultation.getPet().getName());
        emailModel.put("nomeVeterinario", updatedConsultation.getVeterinario().getName());
        emailModel.put("dataConsulta", updatedConsultation.getConsultationdate().format(dateFormatter));
        emailModel.put("horarioConsulta", updatedConsultation.getConsultationtime().format(timeFormatter));
        emailService.sendHtmlEmailFromTemplate(updatedConsultation.getVeterinario().getEmail(), "Alteração de Consulta - Pet Vita", emailModel);

        return consultationMapper.toDTO(updatedConsultation);
    }

    // ... todos os outros métodos do service (findById, findAllForAdmin, etc.) permanecem os mesmos ...

    public List<ConsultationResponseDTO> findForAuthenticatedVeterinary(UserModel user) {
        VeterinaryModel vet = veterinaryRepository.findByUserAccount(user)
                .orElseThrow(() -> new NoSuchElementException("Perfil de veterinário não encontrado para este usuário."));
        return consultationRepository.findByVeterinarioId(vet.getId()).stream()
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConsultationResponseDTO updateConsultationByAdmin(Long consultationId, ConsultationUpdateRequestDTO dto) {
        ConsultationModel consultation = findByIdOrThrow(consultationId);
        if (dto.consultationdate() != null) {
            consultation.setConsultationdate(dto.consultationdate());
        }
        if (dto.consultationtime() != null) {
            consultation.setConsultationtime(dto.consultationtime());
        }
        if (dto.reason() != null && !dto.reason().isEmpty()) {
            consultation.setReason(dto.reason());
        }
        if (dto.observations() != null && !dto.observations().isEmpty()) {
            consultation.setObservations(dto.observations());
        }
        ConsultationModel updatedConsultation = consultationRepository.save(consultation);
        return consultationMapper.toDTO(updatedConsultation);
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