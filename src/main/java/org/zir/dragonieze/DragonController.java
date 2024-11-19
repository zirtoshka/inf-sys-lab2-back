package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.*;
import org.zir.dragonieze.dragon.repo.*;
import org.zir.dragonieze.dto.DragonDTO;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.services.DragonService;
import org.zir.dragonieze.user.UserRepository;
import org.zir.dragonieze.user.User;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/dragon/user/dragon")
public class DragonController extends Controller {

    private final DragonService dragonService;

    public DragonController(BaseService baseService, DragonService dragonService) {
        super(baseService);
        this.dragonService = dragonService;
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        String json = dragonService.addDragon(header, dragon);
        return ResponseEntity.ok(json);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @PathVariable Long id
    ) throws JsonProcessingException {
        service.deleteEntityWithCondition(
                header,
                id,
                Dragon::getUser,
                dragonService.getDragonRepository()
        );
        return ResponseEntity.ok("удалилось ура");
    }


//    @GetMapping("/getDragons")
//    public ResponseEntity<String> getDragons(
//            @RequestHeader(HEADER_AUTH) String header
//    ) throws JsonProcessingException {
//        String username = service.getUsername(header);
//        Optional<User> userOptional = userRepository.findByUsername(username);
//
//        if (!userOptional.isPresent()) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//        User user = userOptional.get();
//        List<Dragon> dragons = dragonRepository.findByUserId(user.getId());
//        List<DragonDTO> dragonDTOs = dragons.stream()
//                .map(dragon -> new DragonDTO(dragon))
//                .collect(Collectors.toList());
//        String json = convertToJson(dragonDTOs);
//        System.out.println("it's method getDragons");
//        return ResponseEntity.ok(json);
//    }


    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updateDragon(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody Dragon dragon
    ) throws JsonProcessingException {
        System.out.println("dsfsf");
        String json = dragonService.updateDragon(header, dragon);
        return ResponseEntity.ok(json);
    }


}

