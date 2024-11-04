package DB.domain;

public class User {
    private int id;
    private String password;
    private int winCount;
    private String name;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.winCount = 0;
    }

    // Getters and Setters
}

