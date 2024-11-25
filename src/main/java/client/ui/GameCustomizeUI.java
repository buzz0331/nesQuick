package client.ui;

import client.thread.MessageReceiver;
import client.ui.customize.cooperationCustomizeUI.CooperationCustomizeUI;
import client.ui.customize.SpeedQuizCustomizeUI;
import client.ui.customize.versusCustomizeUI.VersusCustomizeUI;
import client.ui.icon.ArrowIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameCustomizeUI {
    public GameCustomizeUI(Socket socket, ObjectOutputStream out, String loginUserId, MessageReceiver receiver) {
        JFrame frame = new JFrame("Game Customize");
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

        JLabel customizeLabel = new JLabel("게임 모드를 커스터마이징하세요");
        customizeLabel.setBounds(150, 150, 250, 30);
        customizeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(customizeLabel);

        // 스피드 퀴즈 모드 커스터마이징 버튼
        JButton speedQuizButton = new JButton("스피드 퀴즈 모드");
        speedQuizButton.setBounds(150, 190, 200, 40);
        speedQuizButton.setBackground(new Color(255, 223, 85));
        speedQuizButton.setForeground(Color.BLACK);
        speedQuizButton.setFocusPainted(false);
        speedQuizButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(speedQuizButton);

        // 협동 모드 커스터마이징 버튼
        JButton cooperationModeButton = new JButton("협동 모드");
        cooperationModeButton.setBounds(150, 240, 200, 40);
        cooperationModeButton.setBackground(new Color(255, 223, 85));
        cooperationModeButton.setForeground(Color.BLACK);
        cooperationModeButton.setFocusPainted(false);
        cooperationModeButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(cooperationModeButton);

        // 대전 모드 커스터마이징 버튼
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
                new MenuUI(socket, out, loginUserId, receiver);  // MenuUI로 돌아감
            }
        });

        // 각 게임 모드 커스터마이징 버튼에 대한 이벤트 처리
        speedQuizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new SpeedQuizCustomizeUI(socket, out, loginUserId, receiver);
            }
        });

        cooperationModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new CooperationCustomizeUI(socket, out, loginUserId, receiver);
            }
        });

        versusModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new VersusCustomizeUI(socket, out, loginUserId, receiver);
            }
        });
    }
}
