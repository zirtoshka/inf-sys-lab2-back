package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.DragonHead;
import org.zir.dragonieze.dragon.repo.DragonHeadRepository;
import org.zir.dragonieze.dragon.repo.DragonRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.DragonHeadDTO;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/head")
public class DragonHeadController extends Controller {
    private final DragonHeadRepository headRepository;

    public DragonHeadController(JwtUtil jwtUtil, UserRepository userRepository, DragonHeadRepository headRepository) {
        super(jwtUtil, userRepository);
        this.headRepository = headRepository;
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<String> addHead(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {
        DragonHead savedHead = saveEntityWithUser(header, head, DragonHead::setUser, headRepository);
        String json = getJson(new DragonHeadDTO(savedHead));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getHeads(
            @RequestHeader(HEADER_AUTH) String header
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<DragonHead> heads = headRepository.findByUserId(user.getId());
        List<DragonHeadDTO> headDTOS = heads.stream()
                .map(DragonHeadDTO::new)
                .toList();
        String json = getJson(headDTOS);
        return ResponseEntity.ok(json);
    }


    @Transactional
    @PostMapping("/update")
    public ResponseEntity<String> updateHead(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {
        DragonHead updateHead = updateEntityWithUser(
                header,
                head,
                head.getId(),
                headRepository::findById,
                DragonHead::getUser,
                (old, updated) -> {
                    old.setEyesCount(updated.getEyesCount());
                    old.setCanEdit(updated.getCanEdit());
                },
                headRepository
        );
        String json = getJson(new DragonHeadDTO(updateHead));
        return ResponseEntity.ok(json);
    }

}
