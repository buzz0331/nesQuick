package client.ui.gamemode;

import client.ui.MenuUI;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CooperationUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final int roomId;
    private final String userId;
    private int correctCount = 0;
    private final List<Integer> solvedQuestions = new ArrayList<>();
    private JLabel questionLabel;
    private JLabel imageLabel;
    private JLabel correctCountLabel;
    private JLabel timerLabel;
    private JTextArea chatArea;
    private JTextField messageField;
    private String currentAnswer;
    private JFrame frame;
    private JButton menuButton;
    private Timer timer;

    public CooperationUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, int roomId, String userId) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.roomId = roomId;
        this.userId = userId;

        frame = new JFrame("Cooperation");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        // JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        // logoLabel.setBounds(700, 10, 80, 80);
        // panel.add(logoLabel);

        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        // 문제 및 이미지 영역
        questionLabel = new JLabel("문제가 여기에 표시됩니다.");
        questionLabel.setBounds(50, 50, 600, 30);
        panel.add(questionLabel);

        imageLabel = new JLabel();
        imageLabel.setBounds(50, 90, 300, 200);
        panel.add(imageLabel);

        // 맞춘 문제 수
        correctCountLabel = new JLabel("맞춘 문제 수: 0");
        correctCountLabel.setBounds(650, 20, 120, 30);
        panel.add(correctCountLabel);

        // 타이머 라벨
        timerLabel = new JLabel("남은 시간: 0초");
        timerLabel.setBounds(650, 60, 120, 30);
        panel.add(timerLabel);

        // 채팅 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBounds(50, 300, 700, 200);
        panel.add(chatScroll);

        // 메시지 입력 필드
        messageField = new JTextField();
        messageField.setBounds(50, 510, 600, 30);
        panel.add(messageField);

        // 전송 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(660, 510, 80, 30);
        panel.add(sendButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            stopTimer();  // 타이머 중지
            frame.dispose();
            new RoomListUI(socket, out, in, "Cooperation Mode", userId);
        });

        // 메시지 전송 동작 (정답 제출)
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                checkAnswer(message);
                messageField.setText("");
            }
        });

        // 메뉴로 돌아가기 버튼 초기화
        menuButton = new JButton("Exit");
        menuButton.setBounds(650, 260, 100, 30);  
        menuButton.setVisible(false);  
        menuButton.addActionListener(e -> {
            stopTimer();  // 타이머 중지
            frame.dispose();
            new MenuUI(socket, out, in, userId);
        });
        panel.add(menuButton);

        loadRandomQuestion();  // 시작할 때 첫 번째 문제를 로드합니다.
    }

    private void loadRandomQuestion() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quiz_game.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, quiz, answer, image_url, solved_time FROM Quiz WHERE quiz_set_id = 1 AND id NOT IN (" + 
                     solvedQuestions.toString().replaceAll("[\\[\\]]", "") + ") ORDER BY RANDOM() LIMIT 1")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int questionId = rs.getInt("id");
                String quizText = rs.getString("quiz");
                currentAnswer = rs.getString("answer");  // 현재 문제의 정답을 설정
                String imageUrl = rs.getString("image_url");
                int timeLimit = rs.getInt("solved_time");  // 제한 시간 불러오기

                questionLabel.setText(quizText);

                // 이미지 로딩 확인 및 처리
                try {
                    URL url = new URL(imageUrl);
                    ImageIcon imageIcon = new ImageIcon(url);

                    if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        imageLabel.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH)));
                    } else {
                        imageLabel.setIcon(null);
                        System.out.println("이미지 로드 실패: " + imageUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imageLabel.setIcon(null);
                    System.out.println("이미지 로드 오류: " + imageUrl);
                }

                // 타이머 시작
                startTimer(timeLimit);

                // 현재 문제 ID를 해결된 목록에 추가
                solvedQuestions.add(questionId);
            } else {
                displayCompletionScreen();  // 모든 문제를 풀면 화면에 나타날 메시지와 버튼
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayCompletionScreen() {
        stopTimer();  // 모든 문제를 다 풀면 타이머 중지
        questionLabel.setText("모든 문제를 다 푸셨습니다!");
        imageLabel.setIcon(null);

        // 맞춘 문제 수를 표시
        correctCountLabel.setText("최종 맞춘 문제 수: " + correctCount);

        // 메뉴로 돌아가기 버튼 보이기
        menuButton.setVisible(true);
    }

    private void checkAnswer(String userAnswer) {
        if (userAnswer.equalsIgnoreCase(currentAnswer)) {  // 정답 확인
            correctCount++;
            correctCountLabel.setText("맞춘 문제 수: " + correctCount);
            chatArea.append(userId + ": " + userAnswer + " (정답을 맞췄습니다!)\n");
            if (hasMoreQuestions()) {
                chatArea.append("다음 문제로 넘어갑니다.\n");
                loadRandomQuestion();  // 새로운 문제 로드
            } else {
                displayCompletionScreen();
            }
        } else {
            chatArea.append(userId + ": " + userAnswer + "\n");
        }
    }

    private boolean hasMoreQuestions() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quiz_game.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS remaining FROM Quiz WHERE quiz_set_id = 1 AND id NOT IN (" +
                     solvedQuestions.toString().replaceAll("[\\[\\]]", "") + ")")) {

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("remaining") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startTimer(int seconds) {
        stopTimer();  // 기존 타이머가 있으면 중지
        timerLabel.setText("남은 시간: " + seconds + "초");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int timeLeft = seconds;

            @Override
            public void run() {
                timeLeft--;
                SwingUtilities.invokeLater(() -> timerLabel.setText("남은 시간: " + timeLeft + "초"));

                if (timeLeft <= 0) {
                    stopTimer();
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append("시간이 초과되었습니다. 다음 문제로 넘어갑니다.\n");
                        loadRandomQuestion();
                    });
                }
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
