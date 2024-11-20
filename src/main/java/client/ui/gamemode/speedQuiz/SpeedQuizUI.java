package client.ui.gamemode.speedQuiz;

import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SpeedQuizUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final int roomId;
    private final String userId;
    private Boolean isMaster;
    private final JTextArea chatArea;

    public SpeedQuizUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, int roomId, String userId ,String masterId) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.roomId = roomId;
        this.userId = userId;
        this.isMaster = userId.equals(masterId);

        JFrame frame = new JFrame("Speed Quiz");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeResources(); // 창이 닫힐 때 리소스 정리
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(700, 10, 80, 80);
        panel.add(logoLabel);

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
                sendStartMessage();
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
            frame.dispose();
            new RoomListUI(socket, out, in,"Speed Quiz Mode", userId);
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

        new Thread(this::listenForMessages).start();
    }

    private void  sendStartMessage() {
        try {
            Message startMessage = new Message("start")
                    .setRoomId(roomId)
                    .setUserId(userId);
            out.reset();
            out.writeObject(startMessage);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String messageContent) {
        try {
            Message message = new Message("chat")
                    .setUserId(userId)
                    .setData(messageContent)
                    .setRoomId(roomId);
            System.out.println("채팅 송신");
            out.reset();
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                if(message.getType().equals("chat") && message.getRoomId()==roomId) {
                    chatArea.append(message.getUserId() + ": " + message.getData() + "\n");
                } else if(message.getType().equals("startGameSuccess") && message.getRoomId()==roomId) {
                    System.out.println( message.getData() + "\n");
                    message.getData();
                    new StartSpeedQuiz();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(); // 예외 발생 시 리소스 정리
        }
    }

    private void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
