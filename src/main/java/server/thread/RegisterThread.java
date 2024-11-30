package server.thread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public RegisterThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO User (id, name, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, message.getUserId());
            stmt.setString(2, message.getUsername());
            stmt.setString(3, message.getPassword());
            stmt.executeUpdate();
            message.setData("Registration successful");
            sendResponse();
        } catch (SQLException e) {
            e.printStackTrace();
            message.setData("Registration failed");
            sendResponse();
        }
    }

    private void sendResponse() {
        try {
            out.writeObject(message); // 클라이언트에 결과 전송
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
