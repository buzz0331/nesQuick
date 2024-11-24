package client.ui.gamemode.versusUI;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VersusUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private final String masterId;
    private List<Quiz> list;
    private MessageReceiver receiver;
    private String gameMode;

    private JLabel userCountLabel;
    private int userCount;
    private volatile boolean running = true;
    private final JTextArea chatArea;
    private Thread messageThread;
    private JFrame frame;

    public VersusUI(Socket socket, ObjectOutputStream out, int roomId, String userId, String masterId, MessageReceiver receiver, String gameMode, int userCount) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.receiver = receiver;
        this.masterId = masterId;
        this.gameMode = gameMode;
        this.userCount = userCount;

        frame = new JFrame("Versus");
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

        // 사용자 수 라벨
        userCountLabel = new JLabel("현재 사용자: " + userCount + "명");
        userCountLabel.setBounds(50, 50, 200, 30);
        panel.add(userCountLabel);

        // 방장인 경우
        if (userId.equals(masterId)) {
            // 게임 시작 버튼
            JButton startButton = new JButton("Game Start");
            startButton.setBounds(10, 70, 150, 30);
            panel.add(startButton);

            // 퀴즈 set 리스트 라벨
            JLabel titleLabel = new JLabel("Quiz set 리스트");
            titleLabel.setBounds(200, 50, 200, 30);
            titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
            panel.add(titleLabel);

            // 서버로부터 퀴즈 set들 가져오기
            List<String> quizSets = fetchVersusQuizSetsFromServer();

            // 퀴즈 set 리스트 표시
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String s : quizSets) {
                listModel.addElement(s);
            }
            JList<String> quizSetList = new JList<>(listModel);
            quizSetList.setBounds(50, 100, 500, 200);
            quizSetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            panel.add(quizSetList);

            // 게임 시작 버튼 동작
            startButton.addActionListener(e -> {
                // 비동기로 퀴즈 데이터를 가져오는 작업 실행
                new Thread(() -> {
                    list = fetchVersusQuizListFromServer(quizSetList.getSelectedValue());
                    SwingUtilities.invokeLater(() -> {
                        stopThread();
                        frame.dispose();
                        new VersusStartUI(socket, out, roomId, userId, list, receiver);
                    });
                }).start();

            });

            // 뒤로가기 버튼 동작
            backButton.addActionListener(e -> {
                stopThread();
                outRoom(roomId);
                frame.dispose();
                new RoomListUI(socket, out, "Versus Mode", userId, receiver);
            });
        }

        // 채팅 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBounds(50, 310, 700, 140);
        panel.add(chatScroll);

        // 메시지 입력 필드
        JTextField messageField = new JTextField();
        messageField.setBounds(50, 470, 600, 30);
        panel.add(messageField);

        // 전송 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(660, 470, 80, 30);
        panel.add(sendButton);

        frame.setVisible(true);

        // 방장이 아닌 경우
        if (!userId.equals(masterId)) {
            chatArea.append("Waiting for the host to start the game...\n");

            // 뒤로가기 버튼 동작
            backButton.addActionListener(e -> {
                stopThread();
                outRoom(roomId);
                frame.dispose();
                new RoomListUI(socket, out, "Versus Mode", userId, receiver);
            });
        }

        // 메시지 전송 동작
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageField.setText("");

                // 전송한 메시지를 chatArea에 표시
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
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchVersusQuizSetsFromServer() {
        List<String> quizSetList = new ArrayList<>();
        try {
            Message request = new Message("fetchVersusQuizSets")
                    .setUserId(userId)
                    .setRoomId(roomId)
                    .setData(gameMode);
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();
            String data = response.getData();
            String quizArr[] = data.split("\n");
            for (int i = 0; i < quizArr.length; i++) {
                quizSetList.add(quizArr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quizSetList;
    }

    private List<Quiz> fetchVersusQuizListFromServer(String selectedValue) {
        List<Quiz> quizList = new ArrayList<>();
        try {
            Message request = new Message("fetchVersusQuizList")
                    .setUserId(userId)
                    .setRoomId(roomId)
                    .setData(selectedValue);
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();
            quizList = parseQuizList(response.getData());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return quizList;
    }

    private List<Quiz> parseQuizList(String data) {
        List<Quiz> quizList = new ArrayList<>();
        String[] quizArr = data.split("\n");
        for (String quizData : quizArr) {
            String[] tmp = quizData.split("\t");
            quizList.add(new Quiz(
                    Integer.parseInt(tmp[0]),
                    Integer.parseInt(tmp[1]),
                    tmp[2],
                    tmp[3]
            ));
        }
        return quizList;
    }

    private void outRoom(int roomId) {
        try {
            Message outRequest = new Message("outRoom")
                    .setUserId(userId)
                    .setData(String.valueOf(roomId));
            out.writeObject(outRequest);

            Message response = receiver.takeMessage();
            System.out.println(response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (running) { // 플래그를 사용하여 스레드 종료 여부 확인
                Message message = receiver.takeMessage();
                if ("chat".equals(message.getType()) && message.getRoomId() == roomId) {
                    chatArea.append(message.getUserId() + ": " + message.getData() + "\n");
                } else if ("userEnter".equals(message.getType()) && message.getRoomId() == roomId) {
                    chatArea.append(message.getData() + "\n");
                    userCount++;
                    updateUserCountLabel();
                } else if ("gameStart".equals(message.getType())) {
                    // 게임 시작 관련 데이터 수신
                    list = parseQuizList(message.getData());

                    // 대기 화면 종료 및 게임 시작 화면으로 전환
                    SwingUtilities.invokeLater(() -> {
                        stopThread();
                        frame.dispose();
                        new VersusStartUI(socket, out, roomId, userId, list, receiver);
                    });
                    break; // 게임 시작 메시지 처리 후 루프 종료
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
