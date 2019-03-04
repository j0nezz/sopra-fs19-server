package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Login;
import ch.uzh.ifi.seal.soprafs19.entity.Logout;
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
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@RequestBody User newUser) {
        if(service.usernameAvailable(newUser)) {
            return service.createUser(newUser);
        } else {
            throw new UsernameTakenException("Username already taken");
        }
    }

    @GetMapping("/users/{userId}")
    User getUser(@PathVariable("userId") String id){
        User user = service.getUserById(Long.parseLong(id));

        if(user != null) return user;
        else{
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/users/{userId}")
    ResponseEntity<Void> updateUser(@PathVariable("userId") String id, @RequestBody User updatedUser){
        if( service.updateUser(Long.parseLong(id), updatedUser) ) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "user with userId was not\n" +
                    "found");
        }
    }

    @PostMapping("/login")
    User login(@RequestBody Login data){
        if(service.checkLogin(data.getUsername(), data.getPassword())){
            service.login(data);
            return service.getUserByUsername(data.getUsername());
        } else {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "login failed"
            );
        }
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody Logout data){
        if( service.logout(data)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Could not find a user with matching userId"
            );
        }
    }



}
