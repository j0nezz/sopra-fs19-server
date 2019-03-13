package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.EditUser;
import ch.uzh.ifi.seal.soprafs19.entity.PublicUserData;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {
    private User testUser;


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Test
    public void createUser() {
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        User createdUser = userService.createUser(testUser);

        Assert.assertNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
    }

    @Test
    public void usernameAvailable() {
        Assert.assertNull(userRepository.findByUsername("availableUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("availableUsername");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());

        Assert.assertTrue(userService.usernameAvailable(testUser));
        User createdUser = userService.createUser(testUser);

        Assert.assertFalse(userService.usernameAvailable(createdUser));

    }

    @Test
    public void getUserByUsername() {
        Assert.assertNull(userRepository.findByUsername("UsernameToFind"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("UsernameToFind");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        userService.createUser(testUser);

        Assert.assertEquals(testUser, userRepository.findByUsername("UsernameToFind"));
    }

    @Test
    public void getUserById() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("getUserById");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        PublicUserData createdUser = new PublicUserData(userService.createUser(testUser), false);

        Assert.assertEquals(testUser.getUsername(), createdUser.getUsername());
        Assert.assertEquals(testUser.getName(), createdUser.getName());
        Assert.assertEquals(testUser.getBirthDate(), createdUser.getBirthDate());
        Assert.assertEquals(testUser, userRepository.findById(createdUser.getId()).get());
    }

    @Test
    public void updateUser(){
        // Add initial User
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("originalUsername");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        User createdUser = userService.createUser(testUser);

        // Update user
        EditUser editedUser = new EditUser();
        editedUser.setId(createdUser.getId());
        editedUser.setBirthDate(new Date(0));
        editedUser.setUsername("newUsername");
        userService.updateUser(editedUser);

        Assert.assertEquals(editedUser.getUsername(), userRepository.findById(createdUser.getId()).get().getUsername());
        Assert.assertEquals(editedUser.getBirthDate(), userRepository.findById(createdUser.getId()).get().getBirthDate());

    }




}
