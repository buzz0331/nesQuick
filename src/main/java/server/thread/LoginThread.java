package server.thread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public LoginThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE id = ? AND password = ?")) {
            String userId = message.getUserId();
            stmt.setString(1, userId);
            stmt.setString(2, message.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                message.setData("Login successful");
                message.setUserId(userId);
            } else {
                message.setData("Invalid credentials");
            }
            sendResponse();
        } catch (SQLException e) {
            e.printStackTrace();
            message.setData("Login failed");
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
