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
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user)  {
        Result<User> result = service.create(user);
        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        User created = result.getPayload();
        return new ResponseEntity<>(Map.of("userId", created.getUserId(),"username", created.getUsername()),HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Credentials credentials) {
        Result<User> result = service.login(credentials.username(), credentials.password());
        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        User user = result.getPayload();
        return new ResponseEntity<>(Map.of("userId", user.getUserId(), "username", user.getUsername()),HttpStatus.OK);
    }

        public record Credentials(String username, String password) {}
}
