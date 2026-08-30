package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.shareboard.domain.Result;
import server.shareboard.domain.UserService;
import server.shareboard.models.User;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws NoSuchAlgorithmException {
        Result<User> result = service.create(user);
        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }
}
