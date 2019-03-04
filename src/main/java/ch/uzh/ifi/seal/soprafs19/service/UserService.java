package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.Login;
import ch.uzh.ifi.seal.soprafs19.entity.Logout;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationDate(new Date());
        newUser.setBirthDate(newUser.getBirthDate());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public boolean usernameAvailable(User user) {
        return (userRepository.findByUsername(user.getUsername()) == null );
    }

    public String getToken (String username){
        return (userRepository.findByUsername(username).getToken());
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public User getUserById(long userId){
        return userRepository.findById(userId);
    }

    public boolean checkLogin(String username, String password){
        return (userRepository.findByUsername(username).getPassword().equals(password));
    }
    public void login(Login data){
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

    public boolean updateUser(long userId, User UpdatedUser){
        // Check if Updated User
        if(userId != UpdatedUser.getId()){
            return false;
        }
        User oldUser = getUserById(userId);
        // Check if different fields are set, so we don't ned to send all fields when updating a user.

        if (UpdatedUser.getUsername() != null){
            oldUser.setUsername(UpdatedUser.getUsername());
        }
        if (UpdatedUser.getPassword() != null){
            oldUser.setPassword(UpdatedUser.getPassword());
        }
        if (UpdatedUser.getName() != null){
            oldUser.setName(UpdatedUser.getName());
        }
        if (UpdatedUser.getBirthDate() != null){
            oldUser.setBirthDate(UpdatedUser.getBirthDate());
        }
        userRepository.save(oldUser);
        return true;

    }


}
