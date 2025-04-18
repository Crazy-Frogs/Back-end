package sesi.petvita.user.dto;

import java.util.UUID;

public record UserDTO(String username, String password, UUID id,String phone, String email,String address,int rg) {
}
