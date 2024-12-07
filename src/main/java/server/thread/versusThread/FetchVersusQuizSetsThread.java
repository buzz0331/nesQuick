package server.thread.versusThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Map;

public class FetchVersusQuizSetsThread extends Thread{
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchVersusQuizSetsThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String gameMode = message.getData();
        StringBuilder data = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, quizSet_name FROM QuizSet WHERE game_category = ?")) {
            stmt.setString(1, gameMode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int quizSetId = rs.getInt("id");
                String quizSetName = rs.getString("quizSet_name");
                data.append(quizSetId).append("\t").append(quizSetName).append("\n"); // ID와 이름 모두 추가
            }

            // 응답 메시지 생성 및 전송
            Message response = new Message("fetchVersusQuizSetsResponse")
                    .setData(data.toString());
            out.writeObject(response);
            out.flush();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                Message errorResponse = new Message("fetchRoomListResponse")
                        .setRoomNames(Map.of(-1, "Failed to load rooms...."));
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
