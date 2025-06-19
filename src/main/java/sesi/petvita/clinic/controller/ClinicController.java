package sesi.petvita.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.clinic.dto.ClinicRequestDTO;
import sesi.petvita.clinic.dto.ClinicResponseDTO;
import sesi.petvita.clinic.mapper.ClinicMapper;
import sesi.petvita.clinic.model.ClinicModel;
import sesi.petvita.clinic.repository.ClinicRepository;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.veterinary.model.VeterinaryModel;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clinic")
@Tag(name = "Clinicas", description = "Endpoints relacionados ao cadastro de clinicas")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    @GetMapping
    public ResponseEntity<List<ClinicResponseDTO>> getAllClinic() {
        List<ClinicModel> clinics = clinicRepository.findAll();
        List<ClinicResponseDTO> clinicDTOs = clinics.stream()
                .map(clinicMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clinicDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponseDTO> getClinicById(@PathVariable Long id) {
        return clinicRepository.findById(id)
                .map(clinicMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Adicionar Clinicas")
    public ResponseEntity<ClinicResponseDTO> addClinic(@Valid @RequestBody ClinicRequestDTO clinicRequest) {

        ClinicModel newClinic = clinicMapper.toModel(clinicRequest);
        ClinicModel savedClinic = clinicRepository.save(newClinic);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicMapper.toDTO(savedClinic));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar clinica pelo ID")
    public ResponseEntity<ClinicResponseDTO> updateClinic(@PathVariable Long id, @Valid @RequestBody ClinicRequestDTO clinicRequest) {
        return clinicRepository.findById(id)
                .map(existingClinic -> {
                    existingClinic.setName(clinicRequest.name());
                    existingClinic.setEmail(clinicRequest.email());
                    existingClinic.setPhone(clinicRequest.phone());
                    existingClinic.setAddress(clinicRequest.address());
                    existingClinic.setCareServices(clinicRequest.careServices());
                    existingClinic.setImageurl(clinicRequest.imageurl());

                    ClinicModel updatedClinic = clinicRepository.save(existingClinic);
                    return ResponseEntity.ok(clinicMapper.toDTO(updatedClinic));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar clinica pelo ID")
    public ResponseEntity<Object> deleteClinic(@PathVariable Long id) {
        return clinicRepository.findById(id)
                .map(existing -> {
                    clinicRepository.delete(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}