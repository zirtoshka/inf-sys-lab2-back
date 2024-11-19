package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.zir.dragonieze.dragon.DragonCave;
import org.zir.dragonieze.dragon.repo.DragonCaveRepository;
import org.zir.dragonieze.dto.DragonCaveDTO;
import org.zir.dragonieze.services.BaseService;


@RestController
@RequestMapping("/dragon/user/cave")
@CrossOrigin(origins = "*")

public class DragonCaveController extends Controller {
    private final DragonCaveRepository caveRepository;

    public DragonCaveController(BaseService baseService, DragonCaveRepository caveRepository) {
        super(baseService);
        this.caveRepository = caveRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addCave(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        DragonCave savedCave = service.saveEntityWithUser(header, cave, DragonCave::setUser, caveRepository);
        String json = service.convertToJson(new DragonCaveDTO(savedCave));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getCaves(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
//        String username = service.getUserFromHeader(header);
//        Optional<User> userOptional = userRepository.findByUsername(username);
//
//        if (!userOptional.isPresent()) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//        User user = userOptional.get();
//        List<DragonCave> caves = caveRepository.findByUserId(user.getId());
//        List<DragonCaveDTO> caveDTOS = caves.stream()
//                .map(DragonCaveDTO::new)
//                .collect(Collectors.toList());
//        String json = convertToJson(caveDTOS);
//        System.out.println("it's method getCaves");
        return ResponseEntity.ok("ds");
    }


    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updateCave(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonCave cave
    ) throws JsonProcessingException {
        DragonCave updatedCave = service.updateEntityWithUser(
                header,
                cave,
                cave.getId(),
                caveRepository::findById,
                DragonCave::getUser,
                (old, updates) -> {
                    old.setNumberOfTreasures(updates.getNumberOfTreasures());
                    old.setCanEdit(updates.getCanEdit());
                },
                caveRepository
        );
        String json = service.convertToJson(new DragonCaveDTO(updatedCave));
        return ResponseEntity.ok(json);
    }
}
