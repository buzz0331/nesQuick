package client.ui;

import client.thread.MessageReceiver;
import client.ui.gamemode.CooperationUI;
import client.ui.gamemode.speedQuiz.SpeedQuizUI;
import client.ui.gamemode.versusUI.VersusUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RoomListUI {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String gameMode;
    private String userId;
    private Map<Integer, String> roomMap;

    public RoomListUI(Socket socket, ObjectOutputStream out, String gameMode, String loginUserId, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.gameMode = gameMode;
        this.userId = loginUserId;
        this.roomMap = new HashMap<>();

        JFrame frame = new JFrame("Room List - " + gameMode);
        frame.setSize(600, 500);
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
        logoLabel.setBounds(500, 10, 80, 80);
        panel.add(logoLabel);

        // 방 리스트 라벨
        JLabel titleLabel = new JLabel("방 리스트 - " + gameMode);
        titleLabel.setBounds(200, 50, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel);

        // 서버로부터 방 리스트 가져오기
        fetchRoomListFromServer(receiver);

        // 방 리스트 표시
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Map.Entry<Integer, String> entry : roomMap.entrySet()) {
            listModel.addElement(entry.getValue());
        }
        JList<String> roomList = new JList<>(listModel);
        roomList.setBounds(150, 100, 300, 300);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(roomList);

        // 방 선택 시 해당 UI로 이동
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedRoomName = roomList.getSelectedValue();
                int selectedRoomId = getRoomIdByName(selectedRoomName);
                if (selectedRoomId != -1) {
                    enterRoom(frame, selectedRoomId, receiver);
                }
            }
        });

        // 방 생성 버튼 (+ 아이콘)
        JButton addRoomButton = new JButton("+");
        addRoomButton.setBounds(540, 420, 40, 40);
        addRoomButton.setFont(new Font("Arial", Font.BOLD, 20));
        addRoomButton.setBorderPainted(false);
        addRoomButton.setContentAreaFilled(false);
        addRoomButton.setFocusPainted(false);
        panel.add(addRoomButton);

        addRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomAddUI(socket, out, gameMode, loginUserId, receiver);
            }
        });

        frame.setVisible(true);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new GameModeUI(socket, out, loginUserId, receiver);
            }
        });
    }

    private void enterRoom(JFrame frame, int roomId, MessageReceiver receiver) {
        try {
            Message enterRequest = new Message("enterRoom")
                    .setUserId(userId)
                    .setData(String.valueOf(roomId));
            out.writeObject(enterRequest);

            Message response = receiver.takeMessage();
            if ("enterRoomSuccess".equals(response.getType())) {
                String masterId = response.getRoomMaster();
                switch (gameMode) {
                    case "Speed Quiz Mode":
                        frame.dispose();
                        new SpeedQuizUI(socket, out, roomId, userId, masterId, receiver);
                        break;
                    case "Versus Mode":
                        frame.dispose();
                        new VersusUI(socket, out, roomId, userId, receiver);
                        break;
                    case "Cooperation Mode":
                        frame.dispose();
                        new CooperationUI(socket, out, roomId, userId, receiver);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "알 수 없는 게임 모드입니다.");
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "방 입장 실패: " + response.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getRoomIdByName(String roomName) {
        for (Map.Entry<Integer, String> entry : roomMap.entrySet()) {
            if (entry.getValue().equals(roomName)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void fetchRoomListFromServer(MessageReceiver receiver) {
        try {
            Message request = new Message("fetchRoomList")
                    .setUserId(userId)
                    .setData(gameMode);
            out.writeObject(request);
            out.flush();

            // MessageReceiver에서 메시지를 가져옴
            Message response = receiver.takeMessage();

            if ("fetchRoomListResponse".equals(response.getType())) {
                roomMap = response.getRoomNames();
            } else {
                roomMap.put(-1, "Invalid response");
            }

        } catch (Exception e) {
            e.printStackTrace();
            roomMap.put(-1, "Failed to load rooms");
        }

    }

}
