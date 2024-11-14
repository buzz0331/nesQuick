package client.ui.customize;

import javax.swing.*;

import client.ui.GameCustomizeUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CooperationCustomizeUI {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private Timer timer;

    public CooperationCustomizeUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String loginUserId) {
        JFrame frame = new JFrame("Cooperation Mode Customize");
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 문제 이름 입력 필드
        JLabel questionLabel = new JLabel("문제 이름:");
        questionLabel.setBounds(50, 50, 100, 30);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(questionLabel);

        JTextField questionField = new JTextField();
        questionField.setBounds(150, 50, 300, 30);
        panel.add(questionField);

        // 이미지 URL 입력 필드
        JLabel imageUrlLabel = new JLabel("이미지 URL:");
        imageUrlLabel.setBounds(50, 100, 100, 30);
        imageUrlLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(imageUrlLabel);

        JTextField imageUrlField = new JTextField();
        imageUrlField.setBounds(150, 100, 300, 30);
        panel.add(imageUrlField);

        // 정답 입력 필드
        JLabel answerLabel = new JLabel("정답:");
        answerLabel.setBounds(50, 150, 100, 30);
        answerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(answerLabel);

        JTextField answerField = new JTextField();
        answerField.setBounds(150, 150, 300, 30);
        panel.add(answerField);

        // 제한 시간 입력 필드
        JLabel timeLimitLabel = new JLabel("제한 시간(초):");
        timeLimitLabel.setBounds(50, 200, 100, 30);
        timeLimitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(timeLimitLabel);

        JTextField timeLimitField = new JTextField();
        timeLimitField.setBounds(150, 200, 300, 30);
        panel.add(timeLimitField);

        // 저장 버튼
        JButton saveButton = new JButton("저장");
        saveButton.setBounds(200, 300, 100, 40);
        saveButton.setBackground(new Color(85, 170, 85));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(saveButton);

        frame.setVisible(true);

        // 저장 버튼 동작
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String questionName = questionField.getText();
                String imageUrl = imageUrlField.getText();
                String correctAnswer = answerField.getText();
                int solvedTime = Integer.parseInt(timeLimitField.getText());

                // 데이터베이스에 문제 저장
                saveQuizToDatabase(questionName, correctAnswer, imageUrl, solvedTime);

                // 제한 시간이 설정된 경우 타이머 시작
                startTimer(solvedTime);

                // UI 닫기
                frame.dispose();
                new GameCustomizeUI(socket, out, in, loginUserId);  // 이전 화면으로 돌아감
            }
        });
    }

    // 문제를 quizTable에 저장하는 메서드
    private void saveQuizToDatabase(String quiz, String answer, String imageUrl, int solvedTime) {
        String sql = "INSERT INTO Quiz (quiz_set_id, quiz, answer, image_url, solved_time) VALUES (1, ?, ?, ?, ?)";
        // 일단 quiz_set_id 는 임의로 1로 설정함

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz);
            pstmt.setString(2, answer);
            pstmt.setString(3, imageUrl);
            pstmt.setInt(4, solvedTime);
            pstmt.executeUpdate();
            System.out.println("Quiz saved to database.");
        } catch (SQLException e) {
            System.out.println("Error saving quiz: " + e.getMessage());
        }
    }

    // 제한 시간이 지나면 다음 문제로 넘어가는 타이머 설정 메서드
    private void startTimer(int seconds) {
        timer = new Timer(seconds * 1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("제한 시간이 초과되어 다음 문제로 넘어갑니다.");
                // 다음 문제 로드 메서드 호출 또는 화면 업데이트 로직 추가
                loadNextQuestion();
            }
        });
        timer.setRepeats(false);  // 제한 시간 타이머는 한 번만 실행
        timer.start();
    }

    // 다음 문제를 로드하는 메서드 (구현 필요)
    private void loadNextQuestion() {
        // 실제로 다음 문제를 로드하는 코드 구현
        System.out.println("새로운 문제를 로드합니다.");
        // 예를 들어, CooperationUI의 loadRandomQuestion() 메서드를 호출하도록 설정할 수 있습니다.
    }

    // 제한 시간 타이머 중지 메서드 (필요시 호출하여 타이머 정지)
    private void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            System.out.println("타이머가 중지되었습니다.");
        }
    }
}
