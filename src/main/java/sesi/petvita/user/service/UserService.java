package sesi.petvita.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesi.petvita.admin.dto.UserDetailsWithPetsDTO;
import sesi.petvita.pet.dto.PetResponseDTO;
import sesi.petvita.pet.mapper.PetMapper;
import sesi.petvita.user.dto.UserRequestDTO;
import sesi.petvita.user.dto.UserResponseDTO;
import sesi.petvita.user.dto.UserUpdateRequestDTO;
import sesi.petvita.user.mapper.UserMapper;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.user.role.UserRole;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// NOVO ARQUIVO
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PetMapper petMapper;

    public List<UserResponseDTO> searchByName(String name) {
        return userRepository.findByUsernameContainingIgnoreCase(name)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + id));
    }

    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        // Você pode adicionar uma verificação aqui para ver se o email ou username já existe
        // userRepository.findByEmail(requestDTO.email()).ifPresent(...);

        UserModel user = userMapper.toModel(requestDTO);
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(requestDTO.password()));

        UserModel savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO requestDTO) {
        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + id));

        existingUser.setUsername(requestDTO.username());
        existingUser.setEmail(requestDTO.email());
        existingUser.setPhone(requestDTO.phone());
        existingUser.setAddress(requestDTO.address());
        existingUser.setImageurl(requestDTO.imageurl());

        if (requestDTO.password() != null && !requestDTO.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDTO.password()));
        }

        UserModel savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDetailsWithPetsDTO getUserWithPets(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + userId));

        List<PetResponseDTO> petDTOs = user.getPets().stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());

        return new UserDetailsWithPetsDTO(userMapper.toDTO(user), petDTOs);
    }
}