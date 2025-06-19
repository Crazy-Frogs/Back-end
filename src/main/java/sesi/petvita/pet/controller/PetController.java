package sesi.petvita.pet.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.pet.dto.PetRequestDTO;
import sesi.petvita.pet.dto.PetResponseDTO;
import sesi.petvita.pet.mapper.PetMapper;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pets", description = "Endpoints relacionados aos pets dos usuários")
@RequiredArgsConstructor
public class PetController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetMapper petMapper;

    @GetMapping
    @Operation(summary = "Listar todos os pets")
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {
        List<PetModel> pets = petRepository.findAll();
        List<PetResponseDTO> petDTOs = pets.stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo pet")
    public ResponseEntity<?> createPet(@Valid @RequestBody PetRequestDTO petDto) {
        Optional<UserModel> ownerOptional = userRepository.findById(petDto.usuarioId());
        if (ownerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário dono do pet não encontrado.");
        }
        UserModel owner = ownerOptional.get();

        PetModel pet = petMapper.toModel(petDto);
        pet.setUsuario(owner);

        try {
            PetModel savedPet = petRepository.save(pet);
            return ResponseEntity.status(HttpStatus.CREATED).body(petMapper.toDTO(savedPet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar pet: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet pelo ID")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequestDTO petDto) {
        // --- LÓGICA CORRIGIDA ---
        // 1. Busca o pet que será atualizado
        Optional<PetModel> existingPetOptional = petRepository.findById(id);
        if (existingPetOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet não encontrado.");
        }

        // 2. Busca o novo dono (ou o mesmo) do pet
        Optional<UserModel> newOwnerOptional = userRepository.findById(petDto.usuarioId());
        if (newOwnerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Novo usuário dono do pet não encontrado.");
        }

        PetModel existingPet = existingPetOptional.get();
        UserModel newOwner = newOwnerOptional.get();

        existingPet.setUsuario(newOwner);
        existingPet.setName(petDto.name());
        existingPet.setAge(petDto.age());
        existingPet.setImageurl(petDto.imageurl());
        existingPet.setSpeciespet(petDto.speciespet());

        try {
            PetModel savedPet = petRepository.save(existingPet);
            return ResponseEntity.ok(petMapper.toDTO(savedPet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar pet: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pet pelo ID")
    public ResponseEntity<String> deletePet(@PathVariable Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return ResponseEntity.ok("Pet deletado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pet não encontrado.");
        }
    }
}