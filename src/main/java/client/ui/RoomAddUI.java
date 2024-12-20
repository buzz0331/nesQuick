package client.ui;

import client.thread.MessageReceiver;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RoomAddUI {
    private Socket socket;
    private ObjectOutputStream out;
    private String gameMode;
    private String userId;

    public RoomAddUI(Socket socket, ObjectOutputStream out, String gameMode, String userId, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.gameMode = gameMode;
        this.userId = userId;

        JFrame frame = new JFrame("Create Room - " + gameMode);
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(125, 30, 250, 100);
        panel.add(logoLabel);

        // 게임 모드 라벨
        JLabel modeLabel = new JLabel("게임 모드: " + gameMode);
        modeLabel.setBounds(175, 160, 200, 30);
        modeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        panel.add(modeLabel);

        // 방 이름 입력
        JLabel nameLabel = new JLabel("방 이름:");
        nameLabel.setBounds(150, 200, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(230, 200, 150, 25);
        panel.add(nameText);

        // 인원 제한 입력

        JLabel capacityLabel = new JLabel("인원 제한:");
        capacityLabel.setBounds(150, 240, 80, 25);
        if(!gameMode.equals("Cooperation Mode")) {
            panel.add(capacityLabel);
        }
        JTextField capacityText = new JTextField(20);
        capacityText.setBounds(230, 240, 150, 25);
        if(!gameMode.equals("Cooperation Mode")) {
            panel.add(capacityText);
        }
        // 방 생성 버튼
        JButton createButton = new JButton("방 생성");
        createButton.setBounds(195, 280, 100, 30);
        createButton.setBackground(new Color(255, 223, 85));
        createButton.setForeground(Color.BLACK);
        createButton.setFocusPainted(false);
        panel.add(createButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, gameMode, userId, receiver); // RoomListUI로 돌아감
        });

        // 방 생성 버튼 동작
        createButton.addActionListener(e -> {
            String roomName = nameText.getText();
            String capacityStr = capacityText.getText();
            if(gameMode.equals("Cooperation Mode"))
                capacityStr = "2";

            try {
                int capacity = Integer.parseInt(capacityStr);
                Message createRoomMessage = new Message("createRoom")
                        .setUserId(userId)
                        .setData(gameMode)
                        .setRoomName(roomName)
                        .setCapacity(capacity);  // 인원 제한 전송
                out.writeObject(createRoomMessage);
                Message response = receiver.takeMessage();
                if ("createRoomSuccess".equals(response.getType())) {
                    JOptionPane.showMessageDialog(null, "방 생성 성공!");
                    frame.dispose();
                    new RoomListUI(socket, out, gameMode, userId, receiver);
                } else {
                    JOptionPane.showMessageDialog(null, "방 생성 실패: " + response.getData());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "유효한 인원 제한을 입력하세요.");
            }
        });
    }
}
