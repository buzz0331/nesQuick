package client.ui.gamemode.cooperationUI;

import client.thread.MessageReceiver;
import client.ui.MenuUI;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class CooperationStartUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private final int userNumber;
    private final List<Quiz> quizList;
    private final MessageReceiver receiver;

    private int currentIndex = 0;
    private Timer currentTimer; // 현재 실행 중인 타이머를 추적하기 위한 필드

    private JPanel panel;
    private JFrame frame;
    private JLabel logoLabel;
    private JButton backButton;

    private JTextArea chatArea;
    private JTextField chatField;
    private JButton sendChatButton;

    public CooperationStartUI(Socket socket, ObjectOutputStream out, int roomId, String userId, int userNumber,
                              List<Quiz> quizList, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.userNumber = userNumber;
        this.quizList = quizList;
        this.receiver = receiver;

        this.frame = new JFrame("Cooperation Start");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        this.logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(850, 10, 250, 100); // 우측 상단 배치
        panel.add(logoLabel);

        // 뒤로가기 버튼
        this.backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        displayQuiz(panel, frame, logoLabel, backButton, currentIndex);

        // 채팅 UI
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBounds(850, 150, 300, 400); // 우측 배치
        panel.add(chatScroll);

        chatField = new JTextField();
        chatField.setBounds(850, 570, 220, 30); // 채팅 입력 필드
        panel.add(chatField);

        sendChatButton = new JButton("Send");
        sendChatButton.setBounds(1080, 570, 70, 30); // 채팅 전송 버튼
        panel.add(sendChatButton);

        frame.setVisible(true);
        listenForMessages();

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, "Cooperation Mode", userId, receiver);
        });

        // 채팅 전송 버튼 동작
        sendChatButton.addActionListener(e -> {
            String message = chatField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                chatField.setText("");

                // 전송한 메시지를 chatArea에 표시
                chatArea.append(userId + ": " + message + "\n");
            }
        });
    }

    private void displayQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int index) {
        if (currentTimer != null) {
            currentTimer.stop(); // 기존 타이머 중지
        }

        // 기존 패널에서 퀴즈 관련 요소 제거
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (!(comp instanceof JScrollPane || comp == chatField || comp == sendChatButton || comp == logoLabel || comp == backButton)) {
                panel.remove(comp);
            }
        }

        // 퀴즈 가져오기
        Quiz currentQuiz = quizList.get(index);
        int timeLimit = currentQuiz.getTime();

        // 문제 이미지 로드
        String questionImagePath = "./pics/" + currentQuiz.getQuestion();
        java.io.File imageFile = new java.io.File(questionImagePath);
        if (!imageFile.exists()) {
            JOptionPane.showMessageDialog(frame, "이미지를 찾을 수 없습니다: " + questionImagePath, "오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImageIcon questionImageIcon = new ImageIcon(questionImagePath);
        Image questionImage = questionImageIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        JLabel questionImageLabel = new JLabel(new ImageIcon(questionImage));
        questionImageLabel.setBounds(50, 100, 600, 400);
        panel.add(questionImageLabel);

        // 마스크 패널 추가
        JPanel maskPanelLeft = new JPanel();
        JPanel maskPanelRight = new JPanel();

        maskPanelLeft.setBackground(panel.getBackground());
        maskPanelRight.setBackground(panel.getBackground());

        if (userNumber == 1) {
            maskPanelLeft.setBounds(50, 100, 300, 400);
            maskPanelRight.setBounds(350, 100, 300, 400);
            panel.add(maskPanelRight); // 우측 절반 가리기
            panel.setComponentZOrder(maskPanelRight, 0); // 최상단으로 배치
        } else {
            maskPanelLeft.setBounds(50, 100, 300, 400);
            maskPanelRight.setBounds(350, 100, 300, 400);
            panel.add(maskPanelLeft); // 좌측 절반 가리기
            panel.setComponentZOrder(maskPanelLeft, 0); // 최상단으로 배치
        }

        JLabel ansLabel = new JLabel("정답: ");
        ansLabel.setBounds(50, 520, 60, 30);
        ansLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(ansLabel);

        JTextField ansField = new JTextField();
        ansField.setBounds(110, 520, 400, 30);
        panel.add(ansField);

        JButton sendButton = new JButton("확인");
        sendButton.setBounds(520, 520, 80, 30);
        sendButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        panel.add(sendButton);

        JLabel timerLabel = new JLabel("남은 시간: " + timeLimit + "초");
        timerLabel.setBounds(300, 560, 200, 30);
        timerLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        panel.add(timerLabel);

        panel.revalidate();
        panel.repaint();

        // 타이머 설정
        currentTimer = new Timer(1000, null);
        final int[] timeRemaining = {timeLimit};

        sendButton.addActionListener(e -> {
            currentTimer.stop();
            String userAnswer = ansField.getText();
            if (userAnswer.equals(currentQuiz.getAnswer())) {
                JOptionPane.showMessageDialog(frame, "정답입니다!");
                try {
                    Message nextQuizMessage = new Message("nextCooperationQuiz")
                            .setRoomId(roomId)
                            .setUserId(userId)
                            .setData(String.valueOf(currentIndex + 1));
                    out.writeObject(nextQuizMessage);
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "오답입니다.");
            }
        });

        currentTimer.addActionListener(e -> {
            timeRemaining[0]--;
            timerLabel.setText("남은 시간: " + timeRemaining[0] + "초");

            if (timeRemaining[0] <= 0) {
                currentTimer.stop();
                JOptionPane.showMessageDialog(frame, "시간 초과! 오답 처리됩니다.");
                currentIndex++;
                if (currentIndex < quizList.size()) {
                    displayQuiz(panel, frame, logoLabel, backButton, currentIndex);
                } else {
                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!");
                    frame.dispose();
                    new MenuUI(socket, out, userId, receiver);
                }
            }
        });

        currentTimer.start();
    }

    private void sendMessage(String messageContent) {
        try {
            Message message = new Message("chat")
                    .setUserId(userId)
                    .setData(messageContent)
                    .setRoomId(roomId);
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        Thread messageThread = new Thread(() -> {
            try {
                while (true) {
                    Message message = receiver.takeMessage();
                    if ("chat".equals(message.getType()) && message.getRoomId() == roomId) {
                        chatArea.append(message.getUserId() + ": " + message.getData() + "\n");
                    } else if ("toNextCooperationQuiz".equals(message.getType()) && message.getRoomId() == roomId) {
                        int nextIndex = Integer.parseInt(message.getData());
                        if (nextIndex < quizList.size()) {
                            SwingUtilities.invokeLater(() -> {
                                currentIndex = nextIndex;
                                displayQuiz(panel, frame, logoLabel, backButton, currentIndex);
                            });
                        } else {
                            JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다!");
                            frame.dispose();
                            new MenuUI(socket, out, userId, receiver);
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
