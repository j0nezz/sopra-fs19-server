package ch.uzh.ifi.seal.soprafs19.controller;
import ch.uzh.ifi.seal.soprafs19.entity.AuthRequest;
import ch.uzh.ifi.seal.soprafs19.entity.EditUser;
import ch.uzh.ifi.seal.soprafs19.entity.PublicUserData;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.service.AuthenticationService;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private final UserService service;
    private final AuthenticationService authService;

    UserController(UserService service, AuthenticationService authService) {
        this.service = service;
        this.authService = authService;
    }

    // Return all users
    @GetMapping("/users")
    Iterable<PublicUserData> all(@RequestHeader("Token") String token) {
        if( !authService.checkToken(token)){
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "You need to be logged in!");
        }
        List<PublicUserData> publicUser = new ArrayList<>();
        service.getUsers().forEach( user ->
            publicUser.add(new PublicUserData(user,false ))
        );
        return publicUser;
    }

    // Register User
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    PublicUserData createUser(@RequestBody User newUser) {
        if(service.usernameAvailable(newUser)) {
            return new PublicUserData(service.createUser(newUser), false);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
    }

    // Show specific user
    @GetMapping("/users/{userId}")
    PublicUserData getUser(@PathVariable("userId") String id, @RequestHeader("Token") String token){
        // Check if user is logged in
        if( !authService.checkToken(token)){
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "You need to be logged in!");
        }
        User user = service.getUserById(Long.parseLong(id));
        if(user != null) return new PublicUserData(user, false);
        else{
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

    }
    @PutMapping("/users/{userId}")
    ResponseEntity<Void> updateUser(@PathVariable("userId") String id, @RequestBody EditUser editUser, @RequestHeader("Token") String token){
        // Check if user wants to edit his own profile
        Long editId = Long.parseLong(id);
        if( !authService.checkToken(token)){
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "You need to be logged in!");
        }
        if( editId != editUser.getId()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You're only allowed to edit your own profile!");
        }
        // Check if token matches user to update
        AuthRequest authReq = new AuthRequest(editUser.getId(), token);
        if ( !authService.checkRequest(authReq)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
        }

        // Update user
        if( service.updateUser(editUser) ) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "user with userId was not\n" +
                    "found");
        }
    }

}
