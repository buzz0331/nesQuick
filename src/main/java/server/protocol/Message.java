package server.protocol;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;  // "register", "login" 등
    private String userId;
    private String username;
    private String password;
    private String data; // Message body

    public Message(String type, String userId, String password) {
        this.type = type;
        this.userId = userId;
        this.password = password;
    }

    public Message(String type, String userId, String username, String password) {
        this.type = type;
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getData() {
        return data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setData(String data) {
        this.data = data;
    }
}

