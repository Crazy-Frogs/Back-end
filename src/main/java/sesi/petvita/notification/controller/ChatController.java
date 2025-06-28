package sesi.petvita.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.notification.model.ChatMessage;
import sesi.petvita.notification.service.ChatService; // Crie este serviço
import sesi.petvita.user.model.UserModel;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Endpoints para o chat entre usuário e veterinário")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{consultationId}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable Long consultationId,
            @AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(chatService.getMessages(consultationId, user));
    }

    @PostMapping("/{consultationId}")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable Long consultationId,
            @RequestBody String content,
            @AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(chatService.sendMessage(consultationId, content, user));
    }
}