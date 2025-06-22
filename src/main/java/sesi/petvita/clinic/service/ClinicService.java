package sesi.petvita.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.clinic.dto.ClinicRequestDTO;
import sesi.petvita.clinic.dto.ClinicResponseDTO;
import sesi.petvita.clinic.mapper.ClinicMapper;
import sesi.petvita.clinic.model.ClinicModel;
import sesi.petvita.clinic.repository.ClinicRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// NOVO ARQUIVO (lembre-se de criar o pacote 'service' dentro de 'clinic')
@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    public List<ClinicResponseDTO> findAll() {
        return clinicRepository.findAll().stream()
                .map(clinicMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ClinicResponseDTO findById(Long id) {
        return clinicRepository.findById(id)
                .map(clinicMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Clínica não encontrada com o ID: " + id));
    }

    public ClinicResponseDTO addClinic(ClinicRequestDTO clinicRequest) {
        ClinicModel newClinic = clinicMapper.toModel(clinicRequest);
        ClinicModel savedClinic = clinicRepository.save(newClinic);
        return clinicMapper.toDTO(savedClinic);
    }

    public ClinicResponseDTO updateClinic(Long id, ClinicRequestDTO clinicRequest) {
        ClinicModel existingClinic = clinicRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Clínica não encontrada com o ID: " + id));

        existingClinic.setName(clinicRequest.name());
        existingClinic.setEmail(clinicRequest.email());
        existingClinic.setPhone(clinicRequest.phone());
        existingClinic.setAddress(clinicRequest.address());
        existingClinic.setCareServices(clinicRequest.careServices());
        existingClinic.setImageurl(clinicRequest.imageurl());

        ClinicModel updatedClinic = clinicRepository.save(existingClinic);
        return clinicMapper.toDTO(updatedClinic);
    }

    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new NoSuchElementException("Clínica não encontrada com o ID: " + id);
        }
        clinicRepository.deleteById(id);
    }
}