package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.shareboard.domain.Result;
import server.shareboard.domain.UserService;
import server.shareboard.models.Listing;
import server.shareboard.models.User;

import java.security.NoSuchAlgorithmException;

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
        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) throws NoSuchAlgorithmException {
        Result<User> result = service.create(user);
        if(!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }
}
