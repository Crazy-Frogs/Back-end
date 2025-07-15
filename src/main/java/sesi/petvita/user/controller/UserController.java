package sesi.petvita.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.user.dto.UserRequestDTO;
import sesi.petvita.user.dto.UserResponseDTO;
import sesi.petvita.user.mapper.UserMapper;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para registro e dados do usuário")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper; // Injetar o mapper

    @PostMapping("/register")
    @Operation(summary = "Registrar um novo usuário")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO registeredUser = userService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/me")
    @Operation(summary = "Verificar dados do usuário logado")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserModel user) {
        // 1. O 'user' injetado já é a entidade UserModel completa.
        // 2. Usamos o mapper para converter a entidade para o DTO seguro.
        UserResponseDTO userResponse = userMapper.toDTO(user);

        // 3. Retornamos o DTO, que não tem listas com lazy loading.
        return ResponseEntity.ok(userResponse);
    }
}