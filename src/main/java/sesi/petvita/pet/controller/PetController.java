package sesi.petvita.pet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.pet.dto.PetRequestDTO;
import sesi.petvita.pet.dto.PetResponseDTO;
import sesi.petvita.pet.mapper.PetMapper;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@Tag(name = "Pet", description = "Gerenciamento de Pets")
public class PetController {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    @PostMapping
    @Operation(summary = "Cadastrar um novo pet")
    public ResponseEntity<PetResponseDTO> create(@RequestBody PetRequestDTO dto) {
        PetModel pet = petMapper.toModel(dto);
        PetModel saved = petRepository.save(pet);
        return ResponseEntity.ok(petMapper.toDTO(saved));
    }

    @GetMapping
    @Operation(summary = "Listar todos os pets")
    public ResponseEntity<List<PetResponseDTO>> getAll() {
        List<PetResponseDTO> pets = petRepository.findAll()
                .stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet encontrado"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<PetResponseDTO> getById(@PathVariable Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pet por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<PetResponseDTO> update(@PathVariable Long id, @RequestBody PetRequestDTO dto) {
        return petRepository.findById(id).map(existing -> {
            PetModel updated = petMapper.toModel(dto);
            updated.setId(existing.getId());
            PetModel saved = petRepository.save(updated);
            return ResponseEntity.ok(petMapper.toDTO(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pet por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return petRepository.findById(id).map(pet -> {
            petRepository.delete(pet);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
