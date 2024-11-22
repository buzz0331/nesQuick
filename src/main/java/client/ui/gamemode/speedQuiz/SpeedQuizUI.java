package client.ui.gamemode.speedQuiz;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.gamemode.versusUI.Quiz;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
=======
import java.util.Map;
>>>>>>> main

public class SpeedQuizUI {
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private Boolean isMaster;
    private final JTextArea chatArea;
    private MessageReceiver receiver;
    private Thread messageThread; // 메시지 처리 스레드
    private volatile boolean running = true;
<<<<<<< HEAD
    private List<Quiz> list;

    public SpeedQuizUI(Socket socket, ObjectOutputStream out, int roomId, String userId, String masterId, MessageReceiver receiver) {
=======
    private int userCount;
    private JLabel userCountLabel;

    public SpeedQuizUI(Socket socket, ObjectOutputStream out, int roomId, String userId , String masterId, MessageReceiver receiver, int userCount) {
>>>>>>> main
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

        // 퀴즈 set 리스트 라벨
        JLabel titleLabel = new JLabel("Quiz set 리스트");
        titleLabel.setBounds(200, 50, 200, 30);
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        panel.add(titleLabel);

        // 서버로부터 퀴즈 set들 가져오기
        List<String> quizSets = fetchSpeedQuizSetsFromServer();

        // 퀴즈 set 리스트 표시
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : quizSets) {
            listModel.addElement(s);
        }
        JList<String> quizSetList = new JList<>(listModel);
        quizSetList.setBounds(50, 100, 500, 200);
        quizSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(quizSetList);

        //방장일 경우에 시작 버튼 보이도록
        if (isMaster) {
            JButton startButton = new JButton("Start");
            startButton.setBounds(350, 530, 100, 30);  // 위치와 크기 설정
            startButton.setBackground(new Color(255, 223, 85));
            startButton.setForeground(Color.BLACK);
            startButton.setFocusPainted(false);

            panel.add(startButton);

            startButton.addActionListener(e -> {
                list = fetchSpeedQuizListFromServer(quizSetList.getSelectedValue());
                frame.dispose();
                new StartSpeedQuiz(socket, out, roomId, userId, list, receiver);
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

    private java.util.List<String> fetchSpeedQuizSetsFromServer() {
        java.util.List<String> quizSetList = new ArrayList<>();
        try {
            Message request = new Message("fetchSpeedQuizSets")
                    .setUserId(userId)
                    .setRoomId(roomId);
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();
            String data = response.getData();
            String quizArr[] = data.split("\n");
            for(int i=0;i< quizArr.length;i++){
                quizSetList.add(quizArr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quizSetList;
    }

    private java.util.List<Quiz> fetchSpeedQuizListFromServer(String selectedValue) {
        List<Quiz> quizList = new ArrayList<>();
        try {
            Message request = new Message("fetchSpeedQuizList")
                    .setUserId(userId)
                    .setRoomId(roomId)
                    .setData(selectedValue);
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();
            String data = response.getData();
            String quizArr[] = data.split("\n");
            for(int i=0;i< quizArr.length;i++){
                String tmp[] = quizArr[i].split("\t");
                quizList.add(new Quiz(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]),tmp[2],tmp[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quizList;
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
