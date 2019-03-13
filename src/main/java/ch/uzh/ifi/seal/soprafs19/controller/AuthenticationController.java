package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.Logout;
import ch.uzh.ifi.seal.soprafs19.entity.PublicUserData;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.service.AuthenticationService;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserService userService;

    AuthenticationController(AuthenticationService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    PublicUserData login(@RequestBody User data){
        if(authService.checkLogin(data.getUsername(), data.getPassword())){
            authService.login(data);
            return new PublicUserData(userService.getUserByUsername(data.getUsername()), true);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "login failed"
            );
        }
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody Logout data){
        if( authService.logout(data)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Could not find a user with matching userId"
            );
        }
    }

}
