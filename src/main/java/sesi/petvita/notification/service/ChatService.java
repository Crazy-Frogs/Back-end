package sesi.petvita.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.mapper.ConsultationMapper;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.notification.model.ChatMessage;
import sesi.petvita.notification.repository.ChatMessageRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.role.UserRole;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;
    private final ConsultationMapper consultationMapper;

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

        UserModel receiver;
        UserModel consultationUser = consultation.getUsuario();
        UserModel vetUserAccount = consultation.getVeterinario().getUserAccount();

        if (vetUserAccount == null) {
            throw new IllegalStateException("O veterinário desta consulta não tem uma conta de usuário associada.");
        }

        if (sender.getId().equals(consultationUser.getId())) {
            receiver = vetUserAccount;
        } else {
            receiver = consultationUser;
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .consultation(consultation)
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        notificationService.createNotification(receiver, "Você tem uma nova mensagem de " + sender.getUsername() + ".");

        return savedMessage;
    }

    // Método para buscar todas as conversas para o admin
    public List<ConsultationResponseDTO> getAllConversationsForAdmin() {
        return consultationRepository.findAll().stream()
                // Filtra apenas consultas que já têm mensagens de chat
                .filter(c -> c.getChatMessages() != null && !c.getChatMessages().isEmpty())
                .map(consultationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private ConsultationModel findConsultationById(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + consultationId));
    }

    // Método modificado para dar permissão ao ADMIN
    private boolean isUserAuthorizedForChat(ConsultationModel consultation, UserModel user) {
        // Se o usuário for ADMIN, ele sempre tem permissão.
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        Long consultationUserId = consultation.getUsuario().getId();
        Long vetUserAccountId = (consultation.getVeterinario().getUserAccount() != null)
                ? consultation.getVeterinario().getUserAccount().getId()
                : -1L;

        return user.getId().equals(consultationUserId) || user.getId().equals(vetUserAccountId);
    }
}