package server.thread.speedThread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Map;

public class FetchSpeedQuizListThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchSpeedQuizListThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String quizSetId = message.getData();
        StringBuilder data = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT solved_time, quiz, answer FROM Quiz WHERE quiz_set_id = ?")) {
            stmt.setInt(1, Integer.parseInt(quizSetId)); // ID로 조회
            ResultSet rs = stmt.executeQuery();

            int cnt = 1;
            while (rs.next()) {
                data.append(cnt).append("\t");
                data.append(rs.getInt("solved_time")).append("\t");
                data.append(rs.getString("quiz")).append("\t");
                data.append(rs.getString("answer")).append("\n");
                cnt++;
            }

            Message response = new Message("gameStart")
                    .setUserId(message.getUserId())
                    .setData(data.toString());
            out.writeObject(response);
            out.flush();

            // 방의 다른 클라이언트들에게 퀴즈 리스트 전송
            QuizServer.broadcast(message.getRoomId(), response);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                Message errorResponse = new Message("fetchVersusQuizListResponse")
                        .setRoomNames(Map.of(-1, "Failed to load quiz...."));
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}

