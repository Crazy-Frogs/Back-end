package sesi.petvita.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.user.dto.UserRequestDTO;
import sesi.petvita.user.dto.UserResponseDTO;
import sesi.petvita.user.dto.UserUpdateRequestDTO;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.service.UserService;

import java.util.List;

// ARQUIVO MODIFICADO
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Injeta o Service

    @GetMapping
    @Operation(summary = "Listar todos os usuários (Apenas Admin)")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID (Apenas Admin)")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Verificar dados do usuário logado")
    public ResponseEntity<Object> getCurrentUser(Authentication authentication) {
        // O principal pode ser a sua entidade UserModel, então isso continua funcionando
        return ResponseEntity.ok(authentication.getPrincipal());
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO registeredUser = userService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário pelo ID")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDTO requestDTO) { // Usa o novo DTO seguro
        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário pelo ID (Apenas Admin)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) { // Padronizado para ResponseEntity<Void>
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content
    }
}