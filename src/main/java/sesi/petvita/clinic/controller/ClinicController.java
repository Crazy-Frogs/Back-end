package sesi.petvita.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.clinic.dto.ClinicRequestDTO;
import sesi.petvita.clinic.dto.ClinicResponseDTO;
import sesi.petvita.clinic.service.ClinicService; // Importa o novo service

import java.util.List;

// ARQUIVO MODIFICADO
@RestController
@RequestMapping("/clinic")
@Tag(name = "Clinicas", description = "Endpoints relacionados ao cadastro de clinicas")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService; // Injeta o novo ClinicService

    @GetMapping
    @Operation(summary = "Listar todas as clínicas")
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinic() {
        return ResponseEntity.ok(clinicService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID")
    public ResponseEntity<ClinicResponseDTO> getClinicById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Adicionar uma nova clínica")
    public ResponseEntity<ClinicResponseDTO> addClinic(@Valid @RequestBody ClinicRequestDTO clinicRequest) {
        ClinicResponseDTO createdClinic = clinicService.addClinic(clinicRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClinic);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar clínica pelo ID")
    public ResponseEntity<ClinicResponseDTO> updateClinic(@PathVariable Long id, @Valid @RequestBody ClinicRequestDTO clinicRequest) {
        ClinicResponseDTO updatedClinic = clinicService.updateClinic(id, clinicRequest);
        return ResponseEntity.ok(updatedClinic);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar clínica pelo ID")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build(); // Padronizado para HTTP 204
    }
}