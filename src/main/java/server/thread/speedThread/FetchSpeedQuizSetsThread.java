package server.thread.speedThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Map;

public class FetchSpeedQuizSetsThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchSpeedQuizSetsThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String gameMode = message.getData();
        System.out.println(gameMode);
        String data = "";

        try (Connection conn = DriverManager.getConnection(DB_URL);

             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM QuizSet WHERE game_category = ?")) {
            stmt.setString(1, gameMode);
            ResultSet rs = stmt.executeQuery();


            System.out.println("쿼리 실행됨");

            while (rs.next()) {
                int quizSetId = rs.getInt("id");
                data += quizSetId + "\n";
            }

            System.out.println(data);
            // 응답 메시지 생성 및 전송
            Message response = new Message("fetchSpeedQuizSetsResponse")
                    .setData(data);
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


