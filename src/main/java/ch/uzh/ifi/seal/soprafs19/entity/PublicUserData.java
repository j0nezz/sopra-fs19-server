package ch.uzh.ifi.seal.soprafs19.entity;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;

import java.util.Date;

public class PublicUserData {

    public PublicUserData(User user, boolean includeToken){
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.status = user.getStatus();
        this.creationDate = user.getCreationDate();
        this.birthDate = user.getBirthDate();
        if (includeToken) this.token = user.getToken();
    }

    private Long id;

    private String name;

    private String username;

    private String token;

    private UserStatus status;

    private Date creationDate;

    private Date birthDate;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }
}
