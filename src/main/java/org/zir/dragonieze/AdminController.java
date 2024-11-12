package org.zir.dragonieze;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zir.dragonieze.auth.JwtUtil;
import org.zir.dragonieze.dragon.repo.CoordinatesRepository;
import org.zir.dragonieze.user.UserRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/admin")
public class AdminController extends Controller {
    public AdminController(JwtUtil jwtUtil, UserRepository userRepository) {
        super(jwtUtil, userRepository);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        System.out.println("it's method sayHello");
        return new ResponseEntity<>("{\"message\": \"Hello from secured endpoint\"}", httpHeaders, HttpStatus.OK);
    }
}
