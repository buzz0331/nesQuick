package client.ui.customize.versusCustomizeUI;

import client.ui.MenuUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class VersusCustomizeGetQuizUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final String userId;
    private int num;
    private String sendData;
    public VersusCustomizeGetQuizUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String userId, int num, String sendData) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.userId = userId;
        this.num = num;
        this.sendData = sendData;

        JFrame frame = new JFrame("Versus Customize GetQz");
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

        frame.setVisible(true);

        // 문제 진행을 위한 초기 인덱스
        final int[] currentIndex = {0};

        // 문제 입력 표시 메서드
        displayGetQuiz(panel, frame, logoLabel, backButton, currentIndex, num);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new VersusCustomizeUI(socket, out, in, userId);
        });

    }

    // 문제 표시 메서드
    private void displayGetQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int[] currentIndex, int num) {
        panel.removeAll();

        // 문제
        JLabel qLabel = new JLabel((currentIndex[0] + 1) +"번 문제를 입력하세요");
        qLabel.setBounds(100, 200, 250, 30);
        qLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(qLabel);

        JTextField qField = new JTextField();
        qField.setBounds(300, 200, 300, 30);
        panel.add(qField);

        // 정답
        JLabel aLabel = new JLabel("정답을 입력하세요");
        aLabel.setBounds(100, 300, 250, 30);
        aLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        panel.add(aLabel);

        JTextField aField = new JTextField();
        aField.setBounds(300, 300, 300, 30);
        panel.add(aField);

        // 확인 버튼
        JButton checkButton = new JButton("확인");
        checkButton.setBounds(660, 470, 80, 30);
        checkButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        panel.add(checkButton);

        panel.add(logoLabel);
        panel.add(backButton);
        panel.revalidate();
        panel.repaint();

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendData += qField.getText()+"\n";
                sendData += aField.getText()+"\n";

                currentIndex[0]++;
                if (currentIndex[0] < num) {
                    displayGetQuiz(panel, frame, logoLabel, backButton, currentIndex, num); // 다음 문제 호출
                } else {
                    sendVersusCustomQuizToServer(sendData);
                    JOptionPane.showMessageDialog(frame, "퀴즈 생성이 완료되었습니다.");
                    frame.dispose();
                    new MenuUI(socket, out, in, userId);  // MenuUI로 돌아감
                }
            }
        });
    }

    private void sendVersusCustomQuizToServer(String sendData) {
        try {
            Message request = new Message("sendVersusCustomQuiz")
                    .setUserId(userId)
                    .setData(sendData);
            out.writeObject(request);
            out.flush();

            Message response = (Message) in.readObject();
            System.out.println("퀴즈 생성 완료 응답 받음");
            String data = response.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

