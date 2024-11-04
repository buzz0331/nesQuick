package client.ui;

import server.protocol.Message;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginUI {
    public LoginUI(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        JFrame frame = new JFrame("Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(10, 20, 80, 25);
        panel.add(idLabel);

        JTextField idText = new JTextField(20);
        idText.setBounds(100, 20, 165, 25);
        panel.add(idText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 60, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 100, 100, 25);
        panel.add(loginButton);

        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String password = new String(passwordText.getPassword());

                Message loginMessage = new Message("login", id, password);
                sendMessage(loginMessage, out, in);

                if ("Login successful".equals(loginMessage.getData())) {
                    JOptionPane.showMessageDialog(null, "Login successful");
                    frame.dispose();
                    new GameModeUI(socket, out, in);
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed: " + loginMessage.getData());
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
