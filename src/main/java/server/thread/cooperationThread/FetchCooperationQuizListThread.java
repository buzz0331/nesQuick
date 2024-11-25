package server.thread.cooperationThread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.Map;

public class FetchCooperationQuizListThread extends Thread{
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public FetchCooperationQuizListThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() { // msg에 저장된 퀴즈 set의 이름에 해당하는 퀴즈 리스트 전송
        String quizSetName = message.getData();
/*
        String data = "1\t10\t일감호에 있는 다리 이름은?\t홍예교\n";
        data += "2\t10\t건대 마스코트 거위 이름은?\t건구스\n";
        data += "3\t10\t건대 공학관 건물 번호는?\t21\n";
*/
        String data = "";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT solved_time, quiz, answer FROM Quiz WHERE quiz_set_id = ?")) {
            stmt.setString(1, quizSetName);
            ResultSet rs = stmt.executeQuery();
            int cnt=1;
            while (rs.next()) {
                data += cnt+"\t";
                data += rs.getInt("solved_time")+"\t";
                data += rs.getString("quiz")+"\t";
                data += rs.getString("answer")+"\n";
            }

            // 응답 메시지 생성 및 전송
            Message response = new Message("gameStart")
                    .setUserId(message.getUserId())
                    .setData(data);
            out.writeObject(response);
            out.flush();

            // 방의 다른 클라이언트들에게 퀴즈 리스트 전송
            QuizServer.broadcast(message.getRoomId(), response);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                // 오류 응답 메시지 전송
                Message errorResponse = new Message("fetchCooperationQuizListResponse")
                        .setRoomNames(Map.of(-1, "Failed to load quiz...."));
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
