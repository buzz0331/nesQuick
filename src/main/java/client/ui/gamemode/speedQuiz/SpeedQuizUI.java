package client.ui.gamemode.speedQuiz;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class SpeedQuizUI {
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private Boolean isMaster;
    private final JTextArea chatArea;
    private MessageReceiver receiver;
    private Thread messageThread; // 메시지 처리 스레드
    private volatile boolean running = true;
    private int userCount;
    private JLabel userCountLabel;

    public SpeedQuizUI(Socket socket, ObjectOutputStream out, int roomId, String userId , String masterId, MessageReceiver receiver, int userCount) {
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.isMaster = userId.equals(masterId);
        this.receiver = receiver;
        this.userCount = userCount;

        JFrame frame = new JFrame("Speed Quiz");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(700, 10, 80, 80);
        panel.add(logoLabel);

        // 사용자 수 라벨
        userCountLabel = new JLabel("현재 사용자: " + userCount + "명");
        userCountLabel.setBounds(50, 50, 200, 30);
        panel.add(userCountLabel);


        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        // 채팅 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBounds(50, 100, 700, 350);
        panel.add(chatScroll);

        // 메시지 입력 필드
        JTextField messageField = new JTextField();
        messageField.setBounds(50, 470, 600, 30);
        panel.add(messageField);

        // 전송 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(660, 470, 80, 30);
        panel.add(sendButton);

        //방장일 경우에 시작 버튼 보이도록
        if(isMaster) {
            JButton startButton = new JButton("Start");
            startButton.setBounds(350, 530, 100, 30);  // 위치와 크기 설정
            startButton.setBackground(new Color(255, 223, 85));
            startButton.setForeground(Color.BLACK);
            startButton.setFocusPainted(false);

            panel.add(startButton);

            startButton.addActionListener(e -> {
                frame.dispose();
                new StartSpeedQuiz();
            });
        }
            //일단 방장 없이
//        JButton startButton = new JButton("Start");
//        startButton.setBounds(350, 530, 100, 30);  // 위치와 크기 설정
//        startButton.setBackground(new Color(255, 223, 85));
//        startButton.setForeground(Color.BLACK);
//        startButton.setFocusPainted(false);
//
//        panel.add(startButton);

        frame.setVisible(true);



        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            stopThread();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
            frame.dispose();
            new RoomListUI(socket, out, "Speed Quiz Mode", userId, receiver);
        });

        // 메시지 전송 동작
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageField.setText("");

                //전송한 메시지를 chatArea에 표시
                chatArea.append(userId + ": " + message + "\n");
            }
        });

        messageThread = new Thread(this::listenForMessages);
        messageThread.start();
    }

    private void sendMessage(String messageContent) {
        try {
            Message message = new Message("chat")
                    .setUserId(userId)
                    .setData(messageContent)
                    .setRoomId(roomId);
            System.out.println("채팅 송신");
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (running) { // 플래그를 사용하여 스레드 종료 여부 확인
                Message message = receiver.takeMessage();
                System.out.println("SpeedQuizUI.listenForMessages");
                if ("chat".equals(message.getType()) && message.getRoomId() == roomId) {
                    chatArea.append(message.getUserId() + ": " + message.getData() + "\n");
                } else if ("userEnter".equals(message.getType()) && message.getRoomId() == roomId) {
                    chatArea.append(message.getData() + "\n");
                    userCount++;
                    updateUserCountLabel();
                }
            }
        } catch (Exception e) {
            if (running) { // 실행 중 예외 발생 시 로그 출력
                e.printStackTrace();
            }
        }
    }

    private void stopThread() {
        running = false; // 플래그를 false로 설정
        if (messageThread != null && messageThread.isAlive()) {
            messageThread.interrupt(); // 스레드 인터럽트
        }
    }

    private void updateUserCountLabel() {
        userCountLabel.setText("현재 사용자: " + userCount + "명");
    }
}
