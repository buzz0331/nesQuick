package client.ui;

import client.ui.icon.ArrowIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MenuUI {
    public MenuUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String loginUserId) {
        JFrame frame = new JFrame("Menu");
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

        JLabel menuLabel = new JLabel("Choose Menu");
        menuLabel.setBounds(175, 150, 200, 30);
        menuLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(menuLabel);

        // 게임 실행 버튼
        JButton playGameButton = new JButton("Game Start");
        playGameButton.setBounds(150, 190, 200, 40);
        playGameButton.setBackground(new Color(255, 223, 85));
        playGameButton.setForeground(Color.BLACK);
        playGameButton.setFocusPainted(false);
        playGameButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(playGameButton);

        // 게임 커스터마이징 버튼
        JButton customizeGameButton = new JButton("Game Customize");
        customizeGameButton.setBounds(150, 240, 200, 40);
        customizeGameButton.setBackground(new Color(255, 223, 85));
        customizeGameButton.setForeground(Color.BLACK);
        customizeGameButton.setFocusPainted(false);
        customizeGameButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(customizeGameButton);

        // 로그아웃 버튼
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setBounds(150, 290, 200, 40);
        logoutButton.setBackground(new Color(255, 223, 85));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(logoutButton);

        frame.setVisible(true);

        // 각 버튼에 대한 이벤트 처리
        playGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new GameModeUI(socket, out, in, loginUserId);
            }
        });

        customizeGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new GameCustomizeUI(socket, out, in, loginUserId); // GameCustomizeUI로 이동
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginUI(socket, out, in); // LogoutUI로 이동
            }
        });
    }
}

