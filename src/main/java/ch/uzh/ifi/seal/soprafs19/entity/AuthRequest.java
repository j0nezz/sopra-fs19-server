package ch.uzh.ifi.seal.soprafs19.entity;

public class AuthRequest {
    public AuthRequest(long id, String token){
        this.id = id;
        this.token = token;
    }
    public AuthRequest(){}

    private long id;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
