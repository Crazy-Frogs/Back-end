package sesi.petvita.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.consultation.status.ConsultationStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentReminderService {
    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ConsultationModel> upcomingConsultations = consultationRepository
                .findAllByConsultationdateAndStatus(tomorrow, ConsultationStatus.AGENDADA);

        System.out.println("Executando tarefa agendada: Verificando " + upcomingConsultations.size() + " consultas para amanhã...");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (ConsultationModel consultation : upcomingConsultations) {
            String message = "Lembrete: Você tem uma consulta para o pet " + consultation.getPet().getName() + " amanhã.";

            // 1. Notificação interna
            notificationService.createNotification(consultation.getUsuario(), message);

            // 2. Envio de e-mail com template
            Map<String, Object> emailModel = new HashMap<>();
            emailModel.put("titulo", "Lembrete de Consulta");
            emailModel.put("nomeUsuario", consultation.getUsuario().getUsername());
            emailModel.put("corpoMensagem", "Este é um lembrete amigável sobre sua consulta agendada para amanhã. Por favor, verifique os detalhes abaixo.");
            emailModel.put("mostrarDetalhesConsulta", true);
            emailModel.put("nomePet", consultation.getPet().getName());
            emailModel.put("nomeVeterinario", consultation.getVeterinario().getName());
            emailModel.put("dataConsulta", consultation.getConsultationdate().format(dateFormatter));
            emailModel.put("horarioConsulta", consultation.getConsultationtime().format(timeFormatter));

            emailService.sendHtmlEmailFromTemplate(consultation.getUsuario().getEmail(), "Lembrete de Consulta - Pet Vita", emailModel);
        }
    }
}