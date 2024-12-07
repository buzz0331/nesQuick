package server.thread.cooperationThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SendCooperationCustomQuizThread extends Thread{
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Message message;
    private ObjectOutputStream out;

    public SendCooperationCustomQuizThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String userId = message.getUserId();
        String msg = message.getData();
        String tmp[] = msg.split("\n");
        String gameMode = tmp[0];
        String quizSetTitle = tmp[1];

        System.out.println(msg);

        // DB 연결
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // QuizSet 테이블에 새로운 원소 삽입
            String insertQuizSetSql = "INSERT INTO QuizSet (user_id, game_category, quizSet_name) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuizSetSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, userId);          // user_id
                stmt.setString(2, gameMode);        // game_category
                stmt.setString(3, quizSetTitle);    // quizSet_name (추가된 필드)
                stmt.executeUpdate();

                // 생성된 QuizSet의 ID 가져오기
                ResultSet rs = stmt.getGeneratedKeys();
                int quizSetId = -1;
                if (rs.next()) {
                    quizSetId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve QuizSet ID");
                }

                // Quiz 테이블에 새로운 원소 삽입
                String insertQuizSql = "INSERT INTO Quiz (quiz_set_id, solved_time, quiz, answer, image_url) VALUES (?, ?, ?, ?, NULL)";
                try (PreparedStatement quizStmt = conn.prepareStatement(insertQuizSql)) {
                    for (int i = 2; i < tmp.length; i++) {
                        String quiz[] = tmp[i].split("\t");
                        quizStmt.setInt(1, quizSetId);
                        quizStmt.setInt(2, Integer.parseInt(quiz[0]));     // solved_time
                        quizStmt.setString(3, quiz[1]); // quiz
                        quizStmt.setString(4, quiz[2]); // answer
                        quizStmt.addBatch();
                    }
                    quizStmt.executeBatch();
                }
            }

            // 응답 메시지 생성 및 전송
            Message response = new Message("sendCooperationCustomQuizResponse").setData("ok");
            out.writeObject(response);
            out.flush();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                // 오류 발생 시 실패 메시지 전송
                Message errorResponse = new Message("customQuizFailure").setData("Failed to create quiz set");
                out.writeObject(errorResponse);
                out.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
