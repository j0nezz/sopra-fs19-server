package ch.uzh.ifi.seal.soprafs19.controller;
import ch.uzh.ifi.seal.soprafs19.entity.AuthRequest;
import ch.uzh.ifi.seal.soprafs19.entity.EditUser;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exception.UsernameTakenException;
import ch.uzh.ifi.seal.soprafs19.service.AuthenticationService;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    Iterable<User> all() {
        return service.getUsers();
    }

    // Register User
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@RequestBody User newUser) {
        if(service.usernameAvailable(newUser)) {
            return service.createUser(newUser);
        } else {
            throw new UsernameTakenException("Username already taken");
        }
    }

    // Show specific user
    @GetMapping("/users/{userId}")
    User getUser(@PathVariable("userId") String id, @RequestBody AuthRequest auth){
        // Check if user is logged in
        if( !authService.checkRequest(auth)){
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED);
        }
        User user = service.getUserById(Long.parseLong(id));

        if(user != null) return user;
        else{
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "User not found");
        }

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/users/{userId}")
    ResponseEntity<Void> updateUser(@PathVariable("userId") String id, @RequestBody EditUser editUser){
        // Check if user wants to edit his own profile
        Long editId = Long.parseLong(id);
        if( editId != editUser.getId()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You're only allowed to edit your own profile!");
        }

        // Check if user is logged in
        AuthRequest authReq = new AuthRequest(editUser.getId(), editUser.getToken());
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
