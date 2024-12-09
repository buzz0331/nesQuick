package client.ui;

import client.thread.MessageReceiver;
import client.ui.icon.ArrowIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameModeUI {
    public GameModeUI(Socket socket, ObjectOutputStream out, String loginUserId, MessageReceiver receiver) {
        JFrame frame = new JFrame("Game Modes");
        frame.setSize(500, 400);
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

        JLabel modeLabel = new JLabel("Choose Game Mode");
        modeLabel.setBounds(175, 150, 200, 30);
        modeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        panel.add(modeLabel);

        JButton speedQuizButton = new JButton("Speed Quiz Mode");
        speedQuizButton.setBounds(150, 190, 200, 40);
        speedQuizButton.setBackground(new Color(255, 223, 85));
        speedQuizButton.setForeground(Color.BLACK);
        speedQuizButton.setFocusPainted(false);
        speedQuizButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(speedQuizButton);

        JButton cooperationModeButton = new JButton("Cooperation Mode");
        cooperationModeButton.setBounds(150, 240, 200, 40);
        cooperationModeButton.setBackground(new Color(255, 223, 85));
        cooperationModeButton.setForeground(Color.BLACK);
        cooperationModeButton.setFocusPainted(false);
        cooperationModeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(cooperationModeButton);

        JButton versusModeButton = new JButton("Battle Mode");
        versusModeButton.setBounds(150, 290, 200, 40);
        versusModeButton.setBackground(new Color(255, 223, 85));
        versusModeButton.setForeground(Color.BLACK);
        versusModeButton.setFocusPainted(false);
        versusModeButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(versusModeButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new MenuUI(socket, out, loginUserId ,receiver);  // LoginUI로 돌아감
            }
        });

        // 각 게임 모드 버튼에 대한 이벤트 처리
        speedQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, "Speed Quiz Mode", loginUserId, receiver);
            }
        });

        cooperationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, "Cooperation Mode", loginUserId, receiver);
            }
        });

        versusModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, "Versus Mode", loginUserId, receiver);
            }
        });
    }
}
