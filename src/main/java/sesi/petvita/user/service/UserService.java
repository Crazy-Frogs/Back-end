package sesi.petvita.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesi.petvita.admin.dto.UserDetailsWithPetsDTO;
import sesi.petvita.pet.dto.PetResponseDTO;
import sesi.petvita.pet.mapper.PetMapper;
import sesi.petvita.config.CloudinaryService; // NOVO: Importar CloudinaryService
import sesi.petvita.user.dto.UserRequestDTO;
import sesi.petvita.user.dto.UserResponseDTO;
import sesi.petvita.user.dto.UserUpdateRequestDTO;
import sesi.petvita.user.mapper.UserMapper;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;
import sesi.petvita.user.role.UserRole;

import java.io.IOException; // NOVO: Importar IOException
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PetMapper petMapper;
    private final CloudinaryService cloudinaryService; // NOVO: Injetar o serviço

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
        UserModel user = userMapper.toModel(requestDTO);
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(requestDTO.password()));

        UserModel savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO requestDTO) {
        UserModel existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + id));

        if (requestDTO.username() != null) existingUser.setUsername(requestDTO.username());
        if (requestDTO.email() != null) existingUser.setEmail(requestDTO.email());
        if (requestDTO.phone() != null) existingUser.setPhone(requestDTO.phone());
        if (requestDTO.address() != null) existingUser.setAddress(requestDTO.address());

        // A imagem é atualizada por um endpoint separado, então não mexemos aqui.
        // if (requestDTO.imageurl() != null) existingUser.setImageurl(requestDTO.imageurl());

        if (requestDTO.password() != null && !requestDTO.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDTO.password()));
        }

        UserModel savedUser = userRepository.save(existingUser);
        return userMapper.toDTO(savedUser);
    }

    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + id));

        // NOVO: Deletar imagem do Cloudinary antes de deletar o usuário do banco
        if (user.getImagePublicId() != null && !user.getImagePublicId().isEmpty()) {
            try {
                cloudinaryService.delete(user.getImagePublicId());
            } catch (IOException e) {
                // Loga o erro mas não impede a exclusão do usuário do banco
                System.err.println("Erro ao deletar imagem do usuário no Cloudinary: " + e.getMessage());
            }
        }
        userRepository.delete(user);
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