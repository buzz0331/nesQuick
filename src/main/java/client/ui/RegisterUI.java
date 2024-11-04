package client.ui;

import server.protocol.Message;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegisterUI {
    public RegisterUI(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        JFrame frame = new JFrame("Register");
        frame.setSize(300, 300);
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

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 60, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 60, 165, 25);
        panel.add(nameText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 100, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 100, 165, 25);
        panel.add(passwordText);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 150, 100, 25);
        panel.add(registerButton);

        frame.setVisible(true);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idText.getText();
                String name = nameText.getText();
                String password = new String(passwordText.getPassword());

                Message registerMessage = new Message("register", id, name, password);
                sendMessage(registerMessage, out, in);

                if ("Registration successful".equals(registerMessage.getData())) {
                    JOptionPane.showMessageDialog(null, "Registration successful");
                    frame.dispose();
                    new InitialUI(socket, out, in);
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed: " + registerMessage.getData());
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
