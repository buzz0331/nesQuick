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

    public VersusUI(Socket socket, ObjectOutputStream out, int roomId, String userId, String masterId, MessageReceiver receiver, String gameMode) throws InterruptedException {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.receiver = receiver;
        this.masterId = masterId;
        this.gameMode = gameMode;

        // 방장이 아닌 경우 게임 시작 메세지를 받기 위한 스레드
        Thread receiverThread;

        JFrame frame = new JFrame("Versus");
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


        // 방장인 경우
        if(userId.equals(masterId)){
            // 게임 시작 버튼
            JButton startButton = new JButton("Game Start");
            startButton.setBounds(10,50,150,30);
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
                list = fetchVersusQuizListFromServer(quizSetList.getSelectedValue());
                frame.dispose();
                new VersusStartUI(socket, out, roomId, userId, list, receiver);
            });

            // 뒤로가기 버튼 동작
            backButton.addActionListener(e -> {
                outRoom(roomId);
                frame.dispose();
                new RoomListUI(socket, out, "Versus Mode", userId, receiver);
            });
        }

        // 채팅 영역
        JTextArea chatArea = new JTextArea();
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


        // 방장이 아닌 경우 게임 시작 대기
        if(!userId.equals(masterId)){
            chatArea.append("Waiting for the host to start the game...\n");

            // 별도의 스레드에서 서버 메시지 수신 처리
            receiverThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 서버로부터 메시지 수신
                        Message message = receiver.takeMessage();

                        if ("gameStart".equals(message.getType())) {
                            // 게임 시작 관련 데이터 수신
                            list = parseQuizList(message.getData());

                            // 대기 화면 종료 및 게임 시작 화면으로 전환
                            SwingUtilities.invokeLater(() -> {
                                frame.dispose();
                                new VersusStartUI(socket, out, roomId, userId, list, receiver);
                            });
                            break; // 게임 시작 메시지 처리 후 루프 종료
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt(); // 스레드 종료 신호 처리
                    } catch (Exception e) {
                        e.printStackTrace();
                        break; // 예외 발생 시 루프 종료
                    }
                }
            });
            receiverThread.start();
            // 뒤로가기 버튼 동작
            backButton.addActionListener(e -> {
                receiverThread.interrupt();
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
            }
        });

    }

    private void sendMessage(String messageContent) {
        try {
            Message message = new Message("chat")
                    .setUserId(userId)
                    .setData(messageContent)
                    .setRoomId(roomId);
            out.writeObject(message);
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
            for(int i=0;i< quizArr.length;i++){
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
}