package sesi.petvita.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.admin.dto.MonthlyReportDTO;
import sesi.petvita.admin.dto.UserDetailsWithPetsDTO;
import sesi.petvita.admin.service.AdminReportService;
import sesi.petvita.consultation.dto.ConsultationResponseDTO;
import sesi.petvita.consultation.dto.ConsultationUpdateRequestDTO;
import sesi.petvita.consultation.service.ConsultationService;
import sesi.petvita.notification.service.ChatService;
import sesi.petvita.user.dto.UserResponseDTO;
import sesi.petvita.user.dto.UserUpdateRequestDTO;
import sesi.petvita.user.service.UserService;
import sesi.petvita.veterinary.dto.VeterinaryResponseDTO;
import sesi.petvita.veterinary.service.VeterinaryService;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints exclusivos para administradores")
public class AdminController {

    private final UserService userService;
    private final VeterinaryService veterinaryService;
    private final ConsultationService consultationService;
    private final AdminReportService adminReportService;
    private final ChatService chatService;

    @GetMapping("/users")
    @Operation(summary = "[ADMIN] Listar ou buscar usuários por nome")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(userService.searchByName(name));
        }
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/users/{id}/details")
    @Operation(summary = "[ADMIN] Ver detalhes de um usuário, incluindo seus pets")
    public ResponseEntity<UserDetailsWithPetsDTO> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithPets(id));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "[ADMIN] Atualizar um usuário")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "[ADMIN] Deletar um usuário")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/veterinarians")
    @Operation(summary = "[ADMIN] Listar ou buscar veterinários por nome e/ou especialidade")
    public ResponseEntity<List<VeterinaryResponseDTO>> searchVeterinarians(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) SpecialityEnum speciality) {
        return ResponseEntity.ok(veterinaryService.searchVeterinarians(name, speciality));
    }

    @PutMapping("/veterinarians/{id}")
    @Operation(summary = "[ADMIN] Atualizar os dados de um veterinário")
    public ResponseEntity<VeterinaryResponseDTO> updateVeterinary(@PathVariable Long id, @RequestBody @Valid sesi.petvita.veterinary.dto.VeterinaryRequestDTO dto) {
        return ResponseEntity.ok(veterinaryService.updateVeterinary(id, dto));
    }

    @DeleteMapping("/veterinarians/{id}")
    @Operation(summary = "[ADMIN] Deletar um veterinário e sua conta de usuário")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable Long id) {
        veterinaryService.deleteVeterinary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consultations")
    @Operation(summary = "[ADMIN] Ver todas as consultas do sistema")
    public ResponseEntity<List<ConsultationResponseDTO>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.findAllForAdmin());
    }

    @PutMapping("/consultations/{id}")
    @Operation(summary = "[ADMIN] Atualizar dados de uma consulta")
    public ResponseEntity<ConsultationResponseDTO> updateConsultation(@PathVariable Long id, @RequestBody @Valid ConsultationUpdateRequestDTO dto) {
        return ResponseEntity.ok(consultationService.updateConsultationByAdmin(id, dto));
    }

    // Endpoint para o admin ver a lista de todas as conversas
    @GetMapping("/chats/conversations")
    @Operation(summary = "[ADMIN] Listar todas as conversas do sistema")
    public ResponseEntity<List<ConsultationResponseDTO>> getAllConversations() {
        return ResponseEntity.ok(chatService.getAllConversationsForAdmin());
    }

    @GetMapping("/reports/summary")
    @Operation(summary = "[ADMIN] Ver relatório customizado por período")
    public ResponseEntity<sesi.petvita.admin.dto.ReportSummaryDTO> getReportSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Optional<Long> veterinaryId,
            @RequestParam Optional<SpecialityEnum> speciality) {

        sesi.petvita.admin.dto.ReportSummaryDTO report = adminReportService.getSummaryByDateRange(startDate, endDate, veterinaryId, speciality);
        return ResponseEntity.ok(report);
    }
}