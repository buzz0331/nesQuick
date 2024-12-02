package server.thread.cooperationThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Map;

public class FetchCooperationQuizSetsThread extends Thread{
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchCooperationQuizSetsThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String gameMode = message.getData();
        String data = "";
        /*
        data = "건국대문제\n";
        data += "sample\n";
        */

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM QuizSet WHERE game_category = ?")) {
            stmt.setString(1, gameMode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int quizSetId = rs.getInt("id");
                data += quizSetId+"\n";
            }

            // 응답 메시지 생성 및 전송
            Message response = new Message("fetchCooperationQuizSetsResponse")
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
