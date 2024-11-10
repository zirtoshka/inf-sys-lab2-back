package org.zir.dragonieze;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.dragon.Coordinates;
import org.zir.dragonieze.dragon.DragonHead;
import org.zir.dragonieze.dragon.repo.DragonHeadRepository;
import org.zir.dragonieze.dto.CoordinatesDTO;
import org.zir.dragonieze.dto.DragonHeadDTO;
import org.zir.dragonieze.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/user/head")
public class DragonHeadController extends Controller {
    private final DragonHeadRepository headRepository;

    @Transactional
    @PostMapping("/addHead")
    public ResponseEntity<String> addHead(
            @RequestHeader(HEADER_AUTH) String header,
            @Valid @RequestBody DragonHead head
    ) throws JsonProcessingException {
        String username = getUsername(header, jwtUtil);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        head.setUser(user);
        headRepository.save(head);
        String json = getJson(new DragonHeadDTO(head));
        return ResponseEntity.ok(json);
    }

    @GetMapping("/getHeads")
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

}
