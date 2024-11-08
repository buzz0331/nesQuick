package client.ui;

import client.ui.icon.ArrowIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameModeUI {
    public GameModeUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String loginUserId) {
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

        JLabel modeLabel = new JLabel("게임 모드를 선택하세요");
        modeLabel.setBounds(175, 150, 200, 30);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(modeLabel);

        JButton speedQuizButton = new JButton("스피드 퀴즈 모드");
        speedQuizButton.setBounds(150, 190, 200, 40);
        speedQuizButton.setBackground(new Color(255, 223, 85));
        speedQuizButton.setForeground(Color.BLACK);
        speedQuizButton.setFocusPainted(false);
        speedQuizButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(speedQuizButton);

        JButton cooperationModeButton = new JButton("협동 모드");
        cooperationModeButton.setBounds(150, 240, 200, 40);
        cooperationModeButton.setBackground(new Color(255, 223, 85));
        cooperationModeButton.setForeground(Color.BLACK);
        cooperationModeButton.setFocusPainted(false);
        cooperationModeButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(cooperationModeButton);

        JButton versusModeButton = new JButton("대전 모드");
        versusModeButton.setBounds(150, 290, 200, 40);
        versusModeButton.setBackground(new Color(255, 223, 85));
        versusModeButton.setForeground(Color.BLACK);
        versusModeButton.setFocusPainted(false);
        versusModeButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(versusModeButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginUI(socket, out, in);  // LoginUI로 돌아감
            }
        });

        // 각 게임 모드 버튼에 대한 이벤트 처리
        speedQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, in, "Speed Quiz Mode", loginUserId);
            }
        });

        cooperationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, in, "Cooperation Mode", loginUserId);
            }
        });

        versusModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RoomListUI(socket, out, in, "Versus Mode", loginUserId);
            }
        });
    }
}
