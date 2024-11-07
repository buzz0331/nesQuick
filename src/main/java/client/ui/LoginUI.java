package client.ui;

import client.ui.icon.ArrowIcon;
import protocol.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginUI {
    public LoginUI(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        JFrame frame = new JFrame("Login");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 뒤로가기 버튼 - 기본 UIManager 아이콘 사용
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));  // 크기 20, 검은색
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(125, 30, 250, 100);
        panel.add(logoLabel);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(150, 150, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(230, 150, 150, 25);
        panel.add(idText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 190, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(230, 190, 150, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(150, 240, 200, 50);
        loginButton.setBackground(new Color(255, 223, 85));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(loginButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InitialUI(socket, out, in);  // InitialUI로 돌아감
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String password = new String(passwordText.getPassword());

                Message loginMessage = new Message("login")
                        .setUserId(id)
                        .setPassword(password);
                sendMessage(loginMessage, out, in);

                if ("Login successful".equals(loginMessage.getData())) {
                    JOptionPane.showMessageDialog(null, "로그인 성공");
                    frame.dispose();
                    String loginUserId = loginMessage.getUserId();
                    new GameModeUI(socket, out, in, loginUserId);
                } else {
                    JOptionPane.showMessageDialog(null, "로그인 실패: " + loginMessage.getData());
                }
            }
        });
    }

    private void sendMessage(Message message, ObjectOutputStream out, ObjectInputStream in) {
        try {
            out.writeObject(message);
            Message response = (Message) in.readObject();
            message.setData(response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
