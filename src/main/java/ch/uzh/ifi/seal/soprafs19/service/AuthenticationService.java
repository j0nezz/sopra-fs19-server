package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.AuthRequest;
import ch.uzh.ifi.seal.soprafs19.entity.Logout;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkLogin(String username, String password){
        if(userRepository.findByUsername(username) != null) {
            return (userRepository.findByUsername(username).getPassword().equals(password));
        } else
        {
            return false;
        }
    }
    public void login(User data){
        User user = userRepository.findByUsername(data.getUsername());
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    public boolean logout(Logout data){
        User user = userRepository.findByToken(data.getToken());
        if (user != null){
            user.setStatus(UserStatus.OFFLINE);
            user.setToken(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkRequest (AuthRequest data){
        User user = userRepository.findById(data.getId());
        if( user == null || user.getToken() == null){
            return false;
        } else {
            return (user.getToken().equals(data.getToken()));
        }
    }

    public boolean checkToken(String token){
        return (userRepository.findByToken(token) != null);
    }

}
