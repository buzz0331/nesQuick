package client.ui.customize.speedCustomizeUI;

import client.thread.MessageReceiver;
import client.ui.GameCustomizeUI;
import client.ui.icon.ArrowIcon;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SpeedCustomizeUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final String userId;
    private String sendData;
    private MessageReceiver receiver;

    public SpeedCustomizeUI(Socket socket, ObjectOutputStream out, String userId, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.userId = userId;
        this.receiver = receiver;

        JFrame frame = new JFrame("Speed Customize");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(530, 10, 250, 100);
        panel.add(logoLabel);

        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);



        // 퀴즈 제목
        JLabel titleLabel = new JLabel("퀴즈 제목을 입력하세요");
        titleLabel.setBounds(100, 200, 250, 30);
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(titleLabel);

        JTextField titleField = new JTextField();
        titleField.setBounds(300, 200, 300, 30);
        panel.add(titleField);

        // 문제 수
        JLabel numLabel = new JLabel("문제 수를 선택하세요");
        numLabel.setBounds(100, 300, 250, 30);
        numLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(numLabel);

        String[] numbers = {"3", "4", "5"};
        JComboBox<String> comboBox = new JComboBox<>(numbers);
        comboBox.setBounds(300, 300, 70, 30);
        panel.add(comboBox);

        // 확인 버튼
        JButton checkButton = new JButton("확인");
        checkButton.setBounds(660, 470, 80, 30);
        panel.add(checkButton);

        frame.setVisible(true);


        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameCustomizeUI(socket, out, userId, receiver);
        });

        // 확인 버튼 동작
        checkButton.addActionListener(e -> {
            String title = titleField.getText(); // 제목 입력값
            String selectedValue = (String) comboBox.getSelectedItem();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "퀴즈 제목을 입력해주세요!");
                return;
            }

            sendData = "Speed Mode" + "\n" + title + "\n"; // 제목 포함 데이터 구성
            frame.dispose();
            new SpeedCustomizeGetQuizUI(socket, out, userId, Integer.parseInt(selectedValue), sendData, receiver);
        });


    }
}