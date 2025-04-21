package sesi.petvita.pet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sesi.petvita.pet.breed.*;
import sesi.petvita.pet.gender.Gender;
import sesi.petvita.pet.species.Species;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "pets")
public class PetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3 , max = 50)
    private String name;

    @Min(0)
    private int age;

    @Size(min = 3 , max = 50)
    private String personalizatedSpecies;

    @Size(min = 3 , max = 50)
    private String personalizedBreed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species speciespet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private BirdBreed birdBreed;

    @Enumerated(EnumType.STRING)
    private CatBreed catBreed;

    @Enumerated(EnumType.STRING)
    private DogBreed dogBreed;

    @Enumerated(EnumType.STRING)
    private FishBreed fishBreed;

    @Enumerated(EnumType.STRING)
    private RabbitBreed rabbitBreed;

    @Enumerated(EnumType.STRING)
    private ReptileBreed reptileBreed;

    @Enumerated(EnumType.STRING)
    private RodentBreed rodentBreed;
}



