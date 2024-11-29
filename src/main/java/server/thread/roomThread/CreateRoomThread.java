package server.thread.roomThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateRoomThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public CreateRoomThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String gameMode = message.getData();
        String roomName = message.getRoomName();
        int capacity = message.getCapacity();
        String userId = message.getUserId();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Room (game_category, master_id, name, capacity, current_count) VALUES (?, ?, ?, ?, 0)")) {

            stmt.setString(1, gameMode);
            stmt.setString(2, userId);
            stmt.setString(3, roomName);
            stmt.setInt(4, capacity);
            stmt.executeUpdate();

            // 방 생성 성공 메시지 전송
            Message response = new Message("createRoomSuccess").setData("Room created successfully");
            out.writeObject(response);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                // 오류 발생 시 실패 메시지 전송
                Message errorResponse = new Message("createRoomFailure").setData("Failed to create room");
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
