package sesi.petvita.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.consultation.status.ConsultationStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentReminderService {
    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // Roda todo dia às 08:00 da manhã
    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ConsultationModel> upcomingConsultations = consultationRepository
                .findAllByConsultationdateAndStatus(tomorrow, ConsultationStatus.AGENDADA);

        System.out.println("Executando tarefa agendada: Verificando " + upcomingConsultations.size() + " consultas para amanhã...");

        for (ConsultationModel consultation : upcomingConsultations) {
            String message = "Lembrete: Você tem uma consulta para o pet " + consultation.getPet().getName() + " amanhã, " + consultation.getConsultationdate() + " às " + consultation.getConsultationtime() + ".";

            notificationService.createNotification(consultation.getUsuario(), message);

            emailService.sendEmail(consultation.getUsuario().getEmail(), "Lembrete de Consulta PetVita", message);
        }
    }
}