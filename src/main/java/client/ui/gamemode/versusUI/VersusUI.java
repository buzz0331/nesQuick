package client.ui.gamemode.versusUI;

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

public class VersusUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final int roomId;
    private final String userId;
    private List<Quiz> list;

    public VersusUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, int roomId, String userId) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.roomId = roomId;
        this.userId = userId;

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

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, in, "Versus Mode", userId);
        });

        // 게임 시작 버튼 동작
        startButton.addActionListener(e -> {
            list = fetchVersusQuizListFromServer(quizSetList.getSelectedValue());
            frame.dispose();
            new VersusStartUI(socket, out, in, roomId, userId, list);
        });

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
                    .setRoomId(roomId);
            out.writeObject(request);
            out.flush();

            Message response = (Message) in.readObject();
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

            Message response = (Message) in.readObject();
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
}