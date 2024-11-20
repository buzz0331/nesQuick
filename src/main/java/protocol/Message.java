package protocol;

import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;  // Message 유형 (예시: "register", "login")
    private String userId;
    private String username;
    private String password;

    private String roomName;
    private int roomId;
    private String roomMaster;
    private Map<Integer, String> roomNames;
    private int capacity;
    private String data; //Message body

    public Message(String type) {
        this.type = type;
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

    public Map<Integer, String> getRoomNames() {
        return roomNames;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomMaster() {return roomMaster;}

    public Message setRoomId(int roomId) {
        this.roomId = roomId;
        return this;
    }

    public Message setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public Message setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public Message setType(String type) {
        this.type = type;
        return this;
    }

    public Message setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Message setUsername(String username) {
        this.username = username;
        return this;
    }

    public Message setPassword(String password) {
        this.password = password;
        return this;
    }

    public Message setData(String data) {
        this.data = data;
        return this;
    }

    public Message setRoomNames(Map<Integer, String> roomNames) {
        this.roomNames = roomNames;
        return this;
    }

    public Message setRoomMaster(String roomMaster) {
        this.roomMaster = roomMaster;
        return this;
    }
}