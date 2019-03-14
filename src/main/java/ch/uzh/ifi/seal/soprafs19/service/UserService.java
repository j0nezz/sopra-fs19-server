package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.EditUser;
import ch.uzh.ifi.seal.soprafs19.entity.PublicUserData;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // return all users
    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    // Create user from submitted data, add creation date
    public User createUser(User newUser) {
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationDate(new Date());
        newUser.setBirthDate(newUser.getBirthDate());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    // Check if username is available
    public boolean usernameAvailable(User user) {
        return (userRepository.findByUsername(user.getUsername()) == null );
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User getUserById(long userId){
        return userRepository.findById(userId);
    }
    // Update user data
    public boolean updateUser(EditUser editedUser){
        User oldUser = getUserById(editedUser.getId());
        // Check if different fields are set, so we don't ned to send all fields when updating a user.

        if (editedUser.getUsername() != null){
            oldUser.setUsername(editedUser.getUsername());
        }
        if (editedUser.getBirthDate() != null){
            oldUser.setBirthDate(editedUser.getBirthDate());
        }
        userRepository.save(oldUser);
        return true;
    }


}
