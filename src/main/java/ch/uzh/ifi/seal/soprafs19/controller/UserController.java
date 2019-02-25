package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Login;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exception.UsernameTakenException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import netscape.javascript.JSObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        if(service.usernameAvailable(newUser)) {
            return service.createUser(newUser);
        } else {
            throw new UsernameTakenException("Username already taken");
        }
    }

    @PostMapping("/login")
    User login(@RequestBody Login data){
        if(service.checkLogin(data.getUsername(), data.getPassword())){
            return service.getUserByUsername(data.getUsername());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "login failed"
            );
        }
    }



}
