package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.shareboard.domain.Result;
import server.shareboard.domain.UserService;
import server.shareboard.models.Listing;
import server.shareboard.models.User;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") int id) {
        Result<User> result = service.findById(id);
        if(!result.isSuccess()) {
           return ErrorResponse.build(result);
        }
        User user = result.getPayload();
        return new ResponseEntity<>(
                Map.of("userId", user.getUserId(), "username", user.getUsername()),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable("username") String username){
        Result<User> result = service.findByUsername(username);
        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        User user = result.getPayload();
        return new ResponseEntity<>(
                Map.of("userId", user.getUserId(), "username", user.getUsername()),
                HttpStatus.OK
        );
    }
}
