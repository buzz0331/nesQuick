package client.ui;

import javax.swing.*;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GameModeUI {
    public GameModeUI(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
        JFrame frame = new JFrame("Game Modes");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        panel.setLayout(null);

        JButton speedQuizButton = new JButton("Speed Quiz Mode");
        speedQuizButton.setBounds(50, 20, 200, 30);
        panel.add(speedQuizButton);

        JButton versusModeButton = new JButton("Versus Mode");
        versusModeButton.setBounds(50, 60, 200, 30);
        panel.add(versusModeButton);

        JButton cooperationModeButton = new JButton("Cooperation Mode");
        cooperationModeButton.setBounds(50, 100, 200, 30);
        panel.add(cooperationModeButton);

        frame.setVisible(true);
    }
}

