package client.ui.gamemode.speedQuiz;

import client.thread.MessageReceiver;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class SpeedRankingUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final int roomId;
    private final String userId;
    private List<String> ranking;
    private MessageReceiver receiver;

    public SpeedRankingUI(Socket socket, ObjectOutputStream out, int roomId, String userId, List<String> ranking, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.ranking = ranking;
        this.receiver = receiver;

        JFrame frame = new JFrame("VersusRanking");
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

        // 랭킹
        JLabel headLabel = new JLabel("순위     이름     점수");
        headLabel.setBounds(150, 150, 200, 30);
        headLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        panel.add(headLabel);

        JLabel items[] = new JLabel[ranking.size()];
        for(int i=0;i<ranking.size();i++){
            String tmp[] = ranking.get(i).split("\t");
            items[i] = new JLabel("  "+tmp[0]+"     "+tmp[1]+"     "+tmp[2]);
            items[i].setBounds(150, 200+i*50, 200, 30);
            items[i].setFont(new Font("Malgun Gothic", Font.PLAIN, 20));
            panel.add(items[i]);
        }
        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            outRoom(roomId);
            frame.dispose();
            new RoomListUI(socket, out, "Versus Mode", this.userId, receiver);
        });

    }

    private void outRoom(int roomId) {
        try {
            Message outRequest = new Message("outRoom")
                    .setUserId(userId)
                    .setData(String.valueOf(roomId));
            out.writeObject(outRequest);

            Message response = receiver.takeMessage();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
