package server.thread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FetchRoomListThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchRoomListThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String gameMode = message.getData(); // gameMode 정보 추출
        Map<Integer, String> roomNames = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM Room WHERE game_category = ?")) {
            stmt.setString(1, gameMode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int roomId = rs.getInt("id");
                String roomName = rs.getString("name");
                roomNames.put(roomId, roomName); // room ID와 이름을 맵에 추가
            }

            // 응답 메시지 생성 및 전송
            Message response = new Message("fetchRoomListResponse")
                    .setRoomNames(roomNames);
            out.writeObject(response);
            out.flush();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                // 오류 응답 메시지 전송
                Message errorResponse = new Message("fetchRoomListResponse")
                        .setRoomNames(Map.of(-1, "Failed to load rooms...."));
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
