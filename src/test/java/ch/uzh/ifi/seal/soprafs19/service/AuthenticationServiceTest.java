package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.AuthRequest;
import ch.uzh.ifi.seal.soprafs19.entity.Logout;
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

import static org.junit.Assert.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class AuthenticationServiceTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;



    @Test
    public void checkLogin() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("checkLogin");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());


        Assert.assertFalse(authService.checkLogin(testUser.getUsername(), testUser.getPassword()));

        userService.createUser(testUser);

        Assert.assertTrue(authService.checkLogin(testUser.getUsername(), testUser.getPassword()));
    }

    @Test
    public void loginUser(){
        //Create User
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("authTest");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        User createdUser = userService.createUser(testUser);

        // Check if initially no token exists and user is offline.
        Assert.assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
        Assert.assertNull(userRepository.findByUsername(testUser.getUsername()).getToken());

        // Login User
        authService.login(testUser);

        // Check if user is logged in and a token exists
        Assert.assertEquals(UserStatus.ONLINE, userRepository.findByUsername(testUser.getUsername()).getStatus());
        Assert.assertNotNull(userRepository.findByUsername(testUser.getUsername()).getToken());

    }
    @Test
    public void checkRequest() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("reqTest");
        testUser.setPassword("password");
        testUser.setBirthDate(new Date());
        userService.createUser(testUser);
        authService.login(testUser);

        AuthRequest authReq = new AuthRequest();

        // invalid ID
        authReq.setId(-3);
        authReq.setToken(userRepository.findByUsername("reqTest").getToken());
        Assert.assertFalse(authService.checkRequest(authReq));

        // invalid token
        authReq.setId(userRepository.findByUsername("reqTest").getId());
        authReq.setToken("abc");
        Assert.assertFalse(authService.checkRequest(authReq));

        //Correct Request
        authReq.setToken(userRepository.findByUsername("reqTest").getToken());
        Assert.assertTrue(authService.checkRequest(authReq));


    }

    @Test
    public void logout() {
        Logout logout = new Logout();
        logout.setToken("Invalid Token");

        Assert.assertFalse(authService.logout(logout));

        logout.setToken(userRepository.findByUsername("authTest").getToken());

        Assert.assertTrue(authService.logout(logout));
        Assert.assertEquals(UserStatus.OFFLINE, userRepository.findByUsername("authTest").getStatus());
        Assert.assertNull(userRepository.findByUsername("authTest").getToken());

    }


}