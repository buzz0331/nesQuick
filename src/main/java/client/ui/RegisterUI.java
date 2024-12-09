package client.ui;

import client.thread.MessageReceiver;
import client.ui.icon.ArrowIcon;
import protocol.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegisterUI {
    public RegisterUI(Socket socket, ObjectOutputStream out, MessageReceiver receiver) {
        JFrame frame = new JFrame("Register");
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

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(150, 150, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(230, 150, 150, 25);
        panel.add(idText);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(150, 190, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(230, 190, 150, 25);
        panel.add(nameText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 230, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(230, 230, 150, 25);
        panel.add(passwordText);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBounds(150, 280, 200, 50);
        registerButton.setBackground(new Color(255, 223, 85));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(registerButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InitialUI(socket, out, receiver);  // InitialUI로 돌아감
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String name = nameText.getText();
                String password = new String(passwordText.getPassword());

                Message registerMessage = new Message("register")
                        .setUserId(id)
                        .setPassword(password)
                        .setUsername(name);
                sendMessage(registerMessage, out, receiver);

                if ("Registration successful".equals(registerMessage.getData())) {
                    JOptionPane.showMessageDialog(null, "Registration successful");
                    frame.dispose();
                    new InitialUI(socket, out, receiver);
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed: " + registerMessage.getData());
                }
            }
        });
    }

    private void sendMessage(Message message, ObjectOutputStream out, MessageReceiver receiver) {
        try {
            out.writeObject(message);
            Message response = receiver.takeMessage();
            message.setData(response.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
