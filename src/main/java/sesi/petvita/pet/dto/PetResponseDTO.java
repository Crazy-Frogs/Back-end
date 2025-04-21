package sesi.petvita.pet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import sesi.petvita.pet.breed.*;
import sesi.petvita.pet.gender.Gender;
import sesi.petvita.pet.species.Species;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PetResponseDTO (
        Long id,
        String name,
        int age,
        String personalizatedSpecies,
        String personalizedBreed,
        Species speciespet,
        Gender gender,
        BirdBreed birdBreed,
        CatBreed catBreed,
        DogBreed dogBreed,
        FishBreed fishBreed,
        RabbitBreed rabbitBreed,
        ReptileBreed reptileBreed,
        RodentBreed rodentBreed
){}
