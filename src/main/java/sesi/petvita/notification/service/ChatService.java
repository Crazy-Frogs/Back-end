package sesi.petvita.notification.service;

import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.role.UserRole;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConsultationRepository consultationRepository;
    private final NotificationService notificationService;
    private final Firestore firestore; // Injeção do client do Firestore

    // O método getMessages foi removido.

    public void sendMessage(Long consultationId, String content, UserModel sender) throws AccessDeniedException {
        ConsultationModel consultation = findConsultationById(consultationId);

        // A lógica de permissão continua a mesma
        if (!isUserAuthorizedForChat(consultation, sender)) {
            throw new AccessDeniedException("Você não tem permissão para enviar mensagens neste chat.");
        }

        // Determina quem é o destinatário
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

        // Cria um objeto (Map) para ser salvo no Firestore
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", sender.getId());
        messageData.put("senderName", sender.getUsername()); // Útil para debug no Firebase
        messageData.put("content", content);
        messageData.put("timestamp", com.google.cloud.Timestamp.now()); // Timestamp do servidor

        // Salva a mensagem na coleção do Firestore
        // Estrutura: /consultas/{idDaConsulta}/mensagens/{idDaMensagem}
        firestore.collection("consultas")
                .document(consultationId.toString())
                .collection("mensagens")
                .add(messageData);

        // A notificação interna para o ícone no header continua funcionando
        notificationService.createNotification(receiver, "Você tem uma nova mensagem de " + sender.getUsername() + ".");
    }

    private ConsultationModel findConsultationById(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new NoSuchElementException("Consulta não encontrada com o ID: " + consultationId));
    }

    private boolean isUserAuthorizedForChat(ConsultationModel consultation, UserModel user) {
        // Bloco de debug mantido para ajudar a diagnosticar problemas de permissão
        System.out.println("\n--- Verificando permissão do chat para consulta ID: " + consultation.getId() + " ---");
        System.out.println("ID do Usuário Logado (quem está agindo): " + user.getId());
        System.out.println("Role do Usuário Logado: " + user.getRole());
        System.out.println("ID do Paciente (dono da consulta): " + consultation.getUsuario().getId());
        System.out.println("ID do Veterinário (da conta de usuário): " + (consultation.getVeterinario().getUserAccount() != null ? consultation.getVeterinario().getUserAccount().getId() : "N/A"));
        boolean isOwner = user.getId().equals(consultation.getUsuario().getId());
        boolean isVet = consultation.getVeterinario().getUserAccount() != null && user.getId().equals(consultation.getVeterinario().getUserAccount().getId());
        System.out.println("Resultado da verificação: O usuário é o dono? " + isOwner + ". É o veterinário? " + isVet);
        System.out.println("-----------------------------------------------------\n");

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