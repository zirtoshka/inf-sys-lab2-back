package org.zir.dragonieze;

import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.dragon.repo.AppRepository;
import org.zir.dragonieze.admin.StatusApplication;
import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.user.Role;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/admin")
public class AdminController extends Controller {

    private final AppRepository appRepository;

    public AdminController(JwtUtil jwtUtil, UserRepository userRepository, AppRepository appRepository) {
        super(jwtUtil, userRepository);
        this.appRepository = appRepository;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/app")
    public ResponseEntity<String> changeApplicationStatus(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestBody UpdateAppStatusRequest request
    ) {
        try {
            AdminApplication application = appRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));
            switch(request.getStatus()) {
                case APPROVED -> {
                    User user = application.getUser();
                    if (!user.getRole().toString().contains("ADMIN")){
                        user.setRole(Role.ADMIN);
                        userRepository.save(user);
                    }
                    application.setStatus(StatusApplication.CLOSE);
                }
                case CANCELED -> application.setStatus(StatusApplication.CLOSE);
                default -> throw new IllegalArgumentException("Invalid status value");
            }
            appRepository.save(application);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("{\"message\": \"" + request + "\"}", HttpStatus.OK);
    }
}
