package client.ui.customize.cooperationCustomizeUI;

import client.thread.MessageReceiver;
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

public class CooperationCustomizeGetQuizUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final String userId;
    private int num;
    private String sendData;
    MessageReceiver receiver;

    public CooperationCustomizeGetQuizUI(Socket socket, ObjectOutputStream out, String userId, int num, String sendData, MessageReceiver receiver) {
        this.socket = socket;
        this.out = out;
        this.userId = userId;
        this.num = num;
        this.sendData = sendData;
        this.receiver = receiver;

        JFrame frame = new JFrame("Cooperation Customize");
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
            new CooperationCustomizeUI(socket, out, userId, receiver);
        });

    }

    // 문제 표시 메서드
private void displayGetQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int[] currentIndex, int num) {
    panel.removeAll();

    // 문제 번호
    JLabel qLabel = new JLabel((currentIndex[0] + 1) + "번 문제를 입력하세요");
    qLabel.setBounds(100, 200, 250, 30);
    qLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
    panel.add(qLabel);

    // 문제(사진 업로드)
    JLabel imageLabel = new JLabel("문제 사진을 업로드하세요");
    imageLabel.setBounds(100, 270, 250, 30);
    imageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
    panel.add(imageLabel);

    JTextField imagePathField = new JTextField();
    imagePathField.setBounds(300, 270, 300, 30);
    imagePathField.setEditable(false); // 사진 경로는 수정 불가
    panel.add(imagePathField);

    JButton uploadButton = new JButton("파일 선택");
    uploadButton.setBounds(610, 270, 120, 30);
    uploadButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
    panel.add(uploadButton);

    // 정답
    JLabel aLabel = new JLabel("정답을 입력하세요");
    aLabel.setBounds(100, 340, 250, 30);
    aLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
    panel.add(aLabel);

    JTextField aField = new JTextField();
    aField.setBounds(300, 340, 300, 30);
    panel.add(aField);

    // 시간 제한
    JLabel timeLabel = new JLabel("제한 시간을 입력하세요(초)");
    timeLabel.setBounds(100, 410, 250, 30);
    timeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
    panel.add(timeLabel);

    JTextField timeField = new JTextField();
    timeField.setBounds(300, 410, 300, 30);
    panel.add(timeField);

    // 확인 버튼
    JButton checkButton = new JButton("확인");
    checkButton.setBounds(660, 470, 80, 30);
    checkButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
    panel.add(checkButton);

    panel.add(logoLabel);
    panel.add(backButton);
    panel.revalidate();
    panel.repaint();

// 사진 업로드 버튼 동작
uploadButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("./pics"); // 기본 경로를 pics 폴더로 설정
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // 파일 필터 추가
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".jpg") ||
                       file.getName().toLowerCase().endsWith(".png") ||
                       file.getName().toLowerCase().endsWith(".jpeg");
            }

            @Override
            public String getDescription() {
                return "이미지 파일 (*.jpg, *.jpeg, *.png)";
            }
        });

        // 파일 선택
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            try {
                // 선택된 파일의 절대 경로 및 pics 폴더의 절대 경로 가져오기
                java.nio.file.Path selectedPath = selectedFile.toPath().toRealPath();
                java.nio.file.Path picsPath = new java.io.File("./pics").toPath().toRealPath();

                // pics 내부 파일인지 확인
                if (selectedPath.startsWith(picsPath)) {
                    // 파일 경로를 pics 폴더 기준 상대 경로로 표시
                    java.nio.file.Path relativePath = picsPath.relativize(selectedPath);
                    imagePathField.setText(relativePath.toString());
                } else {
                    JOptionPane.showMessageDialog(frame, "파일은 pics 폴더 내에 있어야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "경로를 확인하는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
});


    // 확인 버튼 동작
    checkButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (imagePathField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "사진을 업로드해주세요.");
                return;
            }
            sendData += timeField.getText() + "\t";
            sendData += imagePathField.getText() + "\t"; // 사진 경로 추가
            sendData += aField.getText() + "\n";

            currentIndex[0]++;
            if (currentIndex[0] < num) {
                displayGetQuiz(panel, frame, logoLabel, backButton, currentIndex, num); // 다음 문제 호출
            } else {
                sendCooperationCustomQuizToServer(sendData);
                JOptionPane.showMessageDialog(frame, "퀴즈 생성이 완료되었습니다.");
                frame.dispose();
                new MenuUI(socket, out, userId, receiver); // MenuUI로 돌아감
            }
        }
    });
}


    private void sendCooperationCustomQuizToServer(String sendData) {
        try {
            Message request = new Message("sendCooperationCustomQuiz")
                    .setUserId(userId)
                    .setData(sendData);
            out.writeObject(request);
            out.flush();

            Message response = receiver.takeMessage();
            System.out.println("퀴즈 생성 완료 응답 받음");
            String data = response.getData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

