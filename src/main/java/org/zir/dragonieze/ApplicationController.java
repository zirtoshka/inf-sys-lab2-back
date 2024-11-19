package org.zir.dragonieze;

import org.zir.dragonieze.admin.AdminApplication;
import org.zir.dragonieze.dragon.repo.AppRepository;
import org.zir.dragonieze.admin.StatusApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.services.BaseService;
import org.zir.dragonieze.user.User;
import org.zir.dragonieze.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("dragon/user/app")
public class ApplicationController extends Controller {
    private final AppRepository appRepository;

    public ApplicationController(BaseService baseService, AppRepository appRepository) {
        super(baseService);
        this.appRepository = appRepository;
    }

    @GetMapping("/newApp")
    public ResponseEntity<String> newApp(
            @RequestHeader(HEADER_AUTH) String header
    ) {

        User user = service.getUserFromHeader(header);
        AdminApplication newApp = new AdminApplication();
        newApp.setCreatedAt(LocalDateTime.now());
        newApp.setStatus(StatusApplication.NEW);
        newApp.setUser(user);

        appRepository.save(newApp);
        return new ResponseEntity<>("New app created", HttpStatus.CREATED);

    }

}
