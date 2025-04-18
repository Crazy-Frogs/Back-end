package sesi.petvita.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.user.model.UserModel;
import sesi.petvita.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/usercontroller")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserModel> addUser(@RequestBody UserModel user) {
        UserModel userModel = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable UUID id, @RequestBody UserModel user) {
        return userRepository.findById(id)
                .map(userModel -> {
                    userModel.setUsername(user.getUsername());
                    userModel.setPassword(user.getPassword());
                    userModel.setEmail(user.getEmail());
                    userModel.setPhone(user.getPhone());
                    userModel.setAddress(user.getAddress());
                    userModel.setRg(user.getRg());
                    return ResponseEntity.ok(userRepository.save(userModel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(userModel -> {
                    userRepository.delete(userModel);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}