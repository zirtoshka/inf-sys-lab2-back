package org.zir.dragonieze.controllers;


import org.zir.dragonieze.admin.UpdateAppStatusRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zir.dragonieze.services.AdminService;
import org.zir.dragonieze.services.BaseService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/dragon/admin")
public class AdminController extends Controller {
    private final AdminService adminService;

    public AdminController(BaseService baseService, AdminService adminService) {
        super(baseService);
        this.adminService = adminService;
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
            adminService.changeApplicationStatus(request);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("{\"message\": \"" + request + "\"}", HttpStatus.OK);
    }
}
