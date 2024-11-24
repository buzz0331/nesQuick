package client.ui.gamemode.speedQuiz;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.gamemode.speedQuiz.Quiz;
import client.ui.gamemode.speedQuiz.SpeedRankingUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StartSpeedQuiz {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private List<Quiz> quizList;
    private int score=0;
    private MessageReceiver receiver;
    private int currentIndex=0;

    private JPanel panel;
    private JFrame frame;
    private JLabel logoLabel;
    private JButton backButton;

    private ActionListener sendButtonListener;

    public StartSpeedQuiz(Socket socket, ObjectOutputStream out, int roomId, String userId, List<Quiz> quizList, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.quizList = quizList;
        this.receiver = receiver;

        this.frame = new JFrame("Speed Start");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        this.logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(530, 10, 250, 100);
        panel.add(logoLabel);

        // 뒤로가기 버튼
        this.backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        frame.setVisible(true);
        listenForMessages();

//        // 문제 진행을 위한 초기 인덱스
//        final int[] currentIndex = {0};

        // 문제 표시 메서드
        displayQuiz(panel, frame, logoLabel, backButton, currentIndex);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, "Versus Mode", userId, receiver);
        });

    }

    // 문제 표시 메서드
    private void displayQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int index) {
        panel.removeAll();

        Quiz currentQuiz = quizList.get(index);
        String question = currentQuiz.getQuestion();
        String answer = currentQuiz.getAnswer();

        JLabel qLabel = new JLabel((index+ 1) + ". " + question);
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

        panel.add(logoLabel);
        panel.add(backButton);
        panel.revalidate();
        panel.repaint();



        sendButton.addActionListener(e -> {
                String userAnswer = ansField.getText();
                if (userAnswer.equals(answer)) {
                    score++;
                    JOptionPane.showMessageDialog(frame, "정답입니다! 현재 점수: " + score);
                    try {
                        // 다음 문제로 넘어가는 메시지를 서버에 전송
                        Message nextQuizMessage = new Message("nextSpeedQuiz")
                                .setRoomId(roomId)
                                .setUserId(userId)
                                .setData(String.valueOf(currentIndex+1));
                        out.writeObject(nextQuizMessage);
                        out.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "오답입니다. 현재 점수: " + score);
                }
//                if (currentIndex[0] < quizList.size()) {
//                    displayQuiz(panel, frame, logoLabel, backButton, currentIndex); // 다음 문제 호출
//                } else {
//                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!\n최종 점수: " + score);
//                    java.util.List<String> list = fetchSpeedRankingFromServer();
//                    frame.dispose();
//                    new VersusRankingUI(socket, out, roomId, userId, list, receiver);
//                }
        });

    }

    private java.util.List<String> fetchSpeedRankingFromServer() {
        List<String> ranking = new ArrayList<>();
        try {
            Message request = new Message("fetchSpeedRanking")
                    .setUserId(userId)
                    .setRoomId(roomId)
                    .setData(score+"");
            out.writeObject(request);
            out.flush();

//            Message response = receiver.takeMessage();
//
//            String data = response.getData();
//            String tmp[] = data.split("\n");
//            for(int i=0;i< tmp.length;i++){
//                ranking.add(tmp[i]);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ranking;
    }

    private void listenForMessages() {
        Thread messageThread = new Thread(() -> {
            try {
                while (true) {
                    Message message = receiver.takeMessage();
                    if ("toNextSpeedQuiz".equals(message.getType()) && message.getRoomId() == roomId) {
                        int nextIndex = Integer.parseInt(message.getData());
                        if (nextIndex < quizList.size()) {
                            SwingUtilities.invokeLater(() -> {
                                currentIndex = nextIndex;
                                displayQuiz(panel, frame, logoLabel, backButton, currentIndex);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!\n최종 점수: " + score);
                                List<String> list = fetchSpeedRankingFromServer();
                                frame.dispose();
                                new SpeedRankingUI(socket, out, roomId, userId, list, receiver);
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        messageThread.start();
    }
}
