package sesi.petvita.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.pet.dto.PetRequestDTO;
import sesi.petvita.pet.dto.PetResponseDTO;
import sesi.petvita.pet.mapper.PetMapper;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.pet.repository.PetRepository;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// NOVO ARQUIVO (lembre-se de criar o pacote 'service' dentro de 'pet')
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetMapper petMapper;

    public List<PetResponseDTO> findAllPets() {
        return petRepository.findAll().stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PetResponseDTO findPetById(Long id) {
        return petRepository.findById(id)
                .map(petMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Pet não encontrado com o ID: " + id));
    }

    public List<PetResponseDTO> findPetsByUser(UserModel user) {
        return petRepository.findByUsuario(user)
                .stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PetResponseDTO createPet(PetRequestDTO petDto) {
        UserModel owner = userRepository.findById(petDto.usuarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuário dono do pet não encontrado com o ID: " + petDto.usuarioId()));

        PetModel pet = petMapper.toModel(petDto);
        pet.setUsuario(owner);
        PetModel savedPet = petRepository.save(pet);
        return petMapper.toDTO(savedPet);
    }

    public PetResponseDTO updatePet(Long id, PetRequestDTO petDto) {
        PetModel existingPet = petRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pet não encontrado com o ID: " + id));

        UserModel owner = userRepository.findById(petDto.usuarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuário dono do pet não encontrado com o ID: " + petDto.usuarioId()));

        existingPet.setName(petDto.name());
        existingPet.setAge(petDto.age());
        existingPet.setImageurl(petDto.imageurl());
        existingPet.setSpeciespet(petDto.speciespet());
        // ... atualize outros campos do pet conforme necessário
        existingPet.setUsuario(owner);

        PetModel updatedPet = petRepository.save(existingPet);
        return petMapper.toDTO(updatedPet);
    }

    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new NoSuchElementException("Pet não encontrado com o ID: " + id);
        }
        petRepository.deleteById(id);
    }
}