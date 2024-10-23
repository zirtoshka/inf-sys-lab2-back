package org.zir.dragonieze;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.dragon.Dragon;
import org.zir.dragonieze.dragon.repo.DragonRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/dragon")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DragonController {
    private final DragonRepository dragonRepository;
    private final CoordinatesRepository coordinatesRepository;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");

//        Dragon dragon = new Dragon();
//        dragon.setName("dragon");
//
//        Coordinates coordinates= new Coordinates();
////        coordinates.setDragon(dragon);
//        coordinates.setX(4d);
//        coordinates.setY(2f);
//        dragon.setCoordinates(coordinates);
//        dragon.setCreationDate(LocalDate.now());
//
//        dragonRepository.save(dragon);

        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/delete-dr")
    public ResponseEntity<String> delete() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method delete");
        dragonRepository.deleteById(3L);
        return new ResponseEntity<>("{\"message\": \"blbla\"}", httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/delete-coord")
    public ResponseEntity<String> deleteCoord() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method delete coordinates");
        coordinatesRepository.deleteById(2L);
        return new ResponseEntity<>("{\"message\": \"blbla\"}", httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/dragons")
    ResponseEntity<String> addDragon(@Valid @RequestBody Dragon dragon) {
        // persisting the dragon
        return ResponseEntity.ok("Dragon is valid");
    }


}