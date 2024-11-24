package client.ui.gamemode.versusUI;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VersusStartUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private List<Quiz> quizList;
    private int score=0;
    private MessageReceiver receiver;

    public VersusStartUI(Socket socket, ObjectOutputStream out, int roomId, String userId, List<Quiz> quizList, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.quizList = quizList;
        this.receiver = receiver;

        JFrame frame = new JFrame("Versus Start");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(530, 10, 250, 100);
        panel.add(logoLabel);

        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        frame.setVisible(true);

        // 문제 진행을 위한 초기 인덱스
        final int[] currentIndex = {0};

        // 문제 표시 메서드
        displayQuiz(panel, frame, logoLabel, backButton, currentIndex);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, "Versus Mode", userId, receiver);
        });

    }

    // 문제 표시 메서드
    private void displayQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int[] currentIndex) {
        panel.removeAll();

        Quiz currentQuiz = quizList.get(currentIndex[0]);
        int timeLimit = currentQuiz.getTime();
        String question = currentQuiz.getQuestion();
        String answer = currentQuiz.getAnswer();

        JLabel qLabel = new JLabel((currentIndex[0] + 1) + ". " + question);
        qLabel.setBounds(175, 170, 500, 50);
        qLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
        panel.add(qLabel);

        JLabel ansLabel = new JLabel("정답: ");
        ansLabel.setBounds(60, 320, 80, 30);
        ansLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(ansLabel);

        JTextField ansField = new JTextField();
        ansField.setBounds(130, 320, 500, 30);
        panel.add(ansField);

        JButton sendButton = new JButton("확인");
        sendButton.setBounds(660, 320, 80, 30);
        sendButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        panel.add(sendButton);

        JLabel timerLabel = new JLabel("남은 시간: " + timeLimit + "초");
        timerLabel.setBounds(330, 380, 150, 30);
        timerLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        panel.add(timerLabel);

        panel.add(logoLabel);
        panel.add(backButton);
        panel.revalidate();
        panel.repaint();

        // Timer 설정
        Timer timer = new Timer(1000, null); // 1초마다 실행
        final int[] timeRemaining = {timeLimit};

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timer.stop(); // 타이머 중지
                String userAnswer = ansField.getText();
                if (userAnswer.equals(answer)) {
                    score++;
                    JOptionPane.showMessageDialog(frame, "정답입니다! 현재 점수: " + score);
                } else {
                    JOptionPane.showMessageDialog(frame, "오답입니다. 현재 점수: " + score);
                }

                // 다음 문제로 이동
                currentIndex[0]++;
                if (currentIndex[0] < quizList.size()) {
                    displayQuiz(panel, frame, logoLabel, backButton, currentIndex);
                } else {
                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!\n최종 점수: " + score);
                    List<String> list = fetchVersusRankingFromServer();
                    frame.dispose();
                    new VersusRankingUI(socket, out, roomId, userId, list, receiver);
                }
            }
        });

        // 타이머 이벤트 추가
        timer.addActionListener(e -> {
            timeRemaining[0]--;
            timerLabel.setText("남은 시간: " + timeRemaining[0] + "초");

            if (timeRemaining[0] <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(frame, "시간 초과! 오답 처리됩니다.");
                // 다음 문제로 이동
                currentIndex[0]++;
                if (currentIndex[0] < quizList.size()) {
                    displayQuiz(panel, frame, logoLabel, backButton, currentIndex);
                } else {
                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!\n최종 점수: " + score);
                    List<String> list = fetchVersusRankingFromServer();
                    frame.dispose();
                    new VersusRankingUI(socket, out, roomId, userId, list, receiver);
                }
            }
        });

        // 타이머 시작
        timer.start();
    }

    private List<String> fetchVersusRankingFromServer() {
        List<String> ranking = new ArrayList<>();
        try {
            Message request = new Message("fetchVersusRanking")
                    .setUserId(userId)
                    .setRoomId(roomId)
                    .setData(score+"");
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();

            String data = response.getData();
            String tmp[] = data.split("\n");
            for(int i=0;i< tmp.length;i++){
                ranking.add(tmp[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ranking;
    }
}
