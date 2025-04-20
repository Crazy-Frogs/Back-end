package sesi.petvita.veterinary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sesi.petvita.veterinary.model.VeterinaryModel;
import sesi.petvita.veterinary.repository.VeterinaryRepository;

import java.util.List;

@RestController
@RequestMapping("/veterinary")
public class VeterinaryController {

    @Autowired
    VeterinaryRepository veterinaryRepository;

    @GetMapping
    public ResponseEntity<List<VeterinaryModel>> getAllVeterinary() {
        List<VeterinaryModel> veterinary = veterinaryRepository.findAll();
        return ResponseEntity.ok().body(veterinary);
    }

    @PostMapping
    public ResponseEntity<VeterinaryModel> addVeterinary(@RequestBody VeterinaryModel veterinary) {
        VeterinaryModel savedVeterinary = veterinaryRepository.save(veterinary);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVeterinary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinaryModel> updateVeterinary(@PathVariable Long id, @RequestBody VeterinaryModel veterinary) {
        return veterinaryRepository.findById(id)
                .map(existing -> {
                    existing.setName(veterinary.getName());
                    existing.setEmail(veterinary.getEmail());
                    existing.setPhone(veterinary.getPhone());
                    existing.setCrmv(veterinary.getCrmv());
                    return ResponseEntity.ok(veterinaryRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVeterinary(@PathVariable Long id, @RequestBody VeterinaryModel veterinary) {
        return veterinaryRepository.findById(id)
                .map(veterinaryModel -> {
                    veterinaryRepository.delete(veterinaryModel);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
