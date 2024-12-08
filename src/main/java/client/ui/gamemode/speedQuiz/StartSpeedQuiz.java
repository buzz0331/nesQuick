package client.ui.gamemode.speedQuiz;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.gamemode.versusUI.VersusRankingUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
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
    private volatile boolean running = true;

    private Thread messageThread; // 메시지 처리 스레드

    private JLabel timerLabel;
    private JPanel panel;
    private JFrame frame;
    private JLabel logoLabel;
    private JButton backButton;
    private Timer currentTimer;

    private ActionListener sendButtonListener;

    public StartSpeedQuiz(Socket socket, ObjectOutputStream out, int roomId, String userId, List<Quiz> quizList, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.quizList = quizList;
        this.receiver = receiver;

        int timeLimit = quizList.get(0).getTime();

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

//        // 문제 진행을 위한 초기 인덱스
//        final int[] currentIndex = {0};

        this.timerLabel = new JLabel("남은 시간: " + timeLimit + "초");
        timerLabel.setBounds(330, 380, 150, 30);
        timerLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        panel.add(timerLabel);

        // 문제 표시 메서드
        displayQuiz(panel, frame, logoLabel, backButton, currentIndex);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, "Speed Mode", userId, receiver);
        });

        this.currentTimer = new Timer(1000, null);
        final int[] timeRemaining = {timeLimit};



        currentTimer.addActionListener(e -> {
            timeRemaining[0]--;
            timerLabel.setText("남은 시간: " + timeRemaining[0] + "초");

            if (timeRemaining[0] <= 0) {
                currentTimer.stop();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!\n최종 점수: " + score);
                    try{
                        Message request = new Message("fetchSpeedRanking")
                                .setRoomId(roomId)
                                .setUserId(userId)
                                .setData(score+"");
                        out.writeObject(request);
                        out.flush();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });

        // 타이머 시작
        currentTimer.start();

        messageThread = new Thread(this::listenForMessages);
        messageThread.start();

    }

    // 문제 표시 메서드
    private void displayQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int index) {
        panel.removeAll();

        Quiz currentQuiz = quizList.get(index);
        String question = currentQuiz.getQuestion();
        String answer = currentQuiz.getAnswer();

        JLabel qLabel = new JLabel("<html><div style='text-align: center;'>"+(index+ 1) + ". " + question+"</div></html>");
        qLabel.setBounds(50, 170, 700, 120);
        qLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
        qLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
        if (timerLabel != null) {
            panel.add(timerLabel);
        }
        panel.revalidate();
        panel.repaint();

        // Timer 설정
//        Timer timer = new Timer(1000, null); // 1초마다 실행
//        currentTimer = timer;
//        final int[] timeRemaining = {timeLimit};

        ActionListener sendNextQuizMessage = e -> {
                try {
                    Message nextQuizMessage = new Message("nextSpeedQuiz")
                            .setRoomId(roomId)
                            .setUserId(userId)
                            .setData(String.valueOf(currentIndex + 1));
                    out.writeObject(nextQuizMessage);
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        };

        sendButton.addActionListener(e -> {
                String userAnswer = ansField.getText();
                if (userAnswer.equals(answer)) {
                    score++;
                    JOptionPane.showMessageDialog(frame, "정답입니다! 현재 점수: " + score);
//                    try {
//                        // 다음 문제로 넘어가는 메시지를 서버에 전송
//                        Message nextQuizMessage = new Message("nextSpeedQuiz")
//                                .setRoomId(roomId)
//                                .setUserId(userId)
//                                .setData(String.valueOf(currentIndex+1));
//                        out.writeObject(nextQuizMessage);
//                        out.flush();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
                    sendNextQuizMessage.actionPerformed(e);
                } else {
                    JOptionPane.showMessageDialog(frame, "오답입니다. 현재 점수: " + score);
                }
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
//        Thread messageThread = new Thread(() -> {
            try {
                while (running) {
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

                                try{
                                    Message request = new Message("fetchSpeedRanking")
                                            .setRoomId(roomId)
                                            .setUserId(userId)
                                            .setData(score+"");
                                    out.writeObject(request);
                                    out.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } else if ("fetchSpeedRankingResponse".equals(message.getType())) {
                        SwingUtilities.invokeLater(() -> {
                            if(currentTimer!=null){
                                currentTimer.stop();
                            }
                            List<String> ranking = new ArrayList<>();
                            String data = message.getData();
                            String tmp[] = data.split("\n");
                            for(int i=0;i< tmp.length;i++){
                                ranking.add(tmp[i]);
                            }
                            stopThread();
                            frame.dispose();
                            new SpeedRankingUI(socket, out, roomId, userId, ranking, receiver);
                        });
                    }
                }
            }  catch (InterruptedException e) {
                // 스레드가 인터럽트되었을 때의 처리
                Thread.currentThread().interrupt();
            }   catch (Exception e) {
                e.printStackTrace();
            }
//        messageThread.start();

    }

    private void stopThread() {
        running = false; // 플래그를 false로 설정
        if (messageThread != null && messageThread.isAlive()) {
            messageThread.interrupt(); // 스레드 인터럽트
        }
    }

}
