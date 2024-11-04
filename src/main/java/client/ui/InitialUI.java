package client.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InitialUI {
    public InitialUI(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        JFrame frame = new JFrame("Quiz Game");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 50, 200, 30);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(50, 100, 200, 30);
        panel.add(registerButton);

        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginUI(socket, out, in);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new RegisterUI(socket, out, in);
            }
        });
    }
}

