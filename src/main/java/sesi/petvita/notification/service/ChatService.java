package sesi.petvita.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.notification.model.ChatMessage;
import sesi.petvita.notification.repository.ChatMessageRepository;
import sesi.petvita.user.model.UserModel;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;


    public List<ChatMessage> getMessages(Long consultationId, UserModel currentUser) throws AccessDeniedException {
        ConsultationModel consultation = findConsultationById(consultationId);

        if (!isUserAuthorizedForChat(consultation, currentUser)) {
            throw new AccessDeniedException("Você não tem permissão para ver este chat.");
        }

        return chatMessageRepository.findByConsultationIdOrderBySentAtAsc(consultationId);
    }


    @Transactional
    public ChatMessage sendMessage(Long consultationId, String content, UserModel sender) throws AccessDeniedException {
        ConsultationModel consultation = findConsultationById(consultationId);

        if (!isUserAuthorizedForChat(consultation, sender)) {
            throw new AccessDeniedException("Você não tem permissão para enviar mensagens neste chat.");
        }

        // Determina o destinatário
        UserModel receiver;
        UserModel consultationUser = consultation.getUsuario();
        UserModel vetUserAccount = consultation.getVeterinario().getUserAccount();

        if (vetUserAccount == null) {
            throw new IllegalStateException("O veterinário desta consulta não tem uma conta de usuário associada.");
        }

        if (sender.getId().equals(consultationUser.getId())) {
            receiver = vetUserAccount; // Se o remetente é o usuário, o destinatário é o veterinário
        } else {
            receiver = consultationUser; // Senão, o destinatário é o usuário
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .consultation(consultation)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Notifica o destinatário
        notificationService.createNotification(receiver, "Você tem uma nova mensagem de " + sender.getUsername() + ".");

        return savedMessage;
    }

    private ConsultationModel findConsultationById(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + consultationId));
    }

    private boolean isUserAuthorizedForChat(ConsultationModel consultation, UserModel user) {
        Long consultationUserId = consultation.getUsuario().getId();
        Long vetUserAccountId = (consultation.getVeterinario().getUserAccount() != null)
                ? consultation.getVeterinario().getUserAccount().getId()
                : -1L; // ID inválido se a conta for nula

        return user.getId().equals(consultationUserId) || user.getId().equals(vetUserAccountId);
    }
}