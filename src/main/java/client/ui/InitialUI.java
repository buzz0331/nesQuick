package client.ui;

import client.thread.MessageReceiver;
import client.ui.LoginUI;
import client.ui.RegisterUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InitialUI {
    public InitialUI(Socket socket, ObjectOutputStream out, MessageReceiver receiver) {
        JFrame frame = new JFrame("Quiz Game");
        frame.setSize(500, 400);  // 창 크기 조절
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));  // 배경색 설정 (밝은 노란색)
        frame.add(panel);

        // 로고 이미지 크기 조정 및 추가
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(125, 30, 250, 100);  // 위치 조정
        panel.add(logoLabel);

        // 로그인 버튼
        JButton loginButton = new JButton("Log in");
        loginButton.setBounds(150, 150, 200, 50);
        loginButton.setBackground(new Color(255, 223, 85));  // 버튼 색상 설정
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(loginButton);

        // 회원가입 버튼
        JButton registerButton = new JButton("Sign Up");
        registerButton.setBounds(150, 220, 200, 50);
        registerButton.setBackground(new Color(255, 223, 85));  // 버튼 색상 설정
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(registerButton);

        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginUI(socket, out, receiver);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RegisterUI(socket, out, receiver);
            }
        });
    }
}
