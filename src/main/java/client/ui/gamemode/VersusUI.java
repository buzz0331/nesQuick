package client.ui.gamemode;

import client.ui.MenuUI;
import client.ui.RoomListUI;
import client.ui.icon.ArrowIcon;
import protocol.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class VersusUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final int roomId;
    private final String userId;
    private List<quiz> list;

    public VersusUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, int roomId, String userId) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.roomId = roomId;
        this.userId = userId;

        JFrame frame = new JFrame("Versus");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(500, 10, 200, 80);
        panel.add(logoLabel);

        // 뒤로가기 버튼
        JButton backButton = new JButton(new ArrowIcon(20, Color.BLACK));
        backButton.setBounds(10, 10, 30, 30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        // 게임 시작 버튼
        JButton startButton = new JButton("Game Start");
        startButton.setBounds(10,50,150,30);
        panel.add(startButton);

        // 채팅 영역
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBounds(50, 100, 700, 350);
        panel.add(chatScroll);

        // 메시지 입력 필드
        JTextField messageField = new JTextField();
        messageField.setBounds(50, 470, 600, 30);
        panel.add(messageField);

        // 전송 버튼
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(660, 470, 80, 30);
        panel.add(sendButton);

        frame.setVisible(true);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, in, "Versus Mode", userId);
        });

        // 게임 시작 버튼 동작
        startButton.addActionListener(e -> {
            list = fetchVersusQuizListFromServer();
            frame.dispose();
            new VersusStartUI(socket, out, in, roomId, userId, list);
        });

        // 메시지 전송 동작
        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageField.setText("");
            }
        });

    }

    private void sendMessage(String messageContent) {
        try {
            Message message = new Message("chat")
                    .setUserId(userId)
                    .setData(messageContent)
                    .setRoomId(roomId);
            out.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<quiz> fetchVersusQuizListFromServer() {
        List<quiz> quizList = new ArrayList<>();
        try {
            Message request = new Message("fetchVersusQuizList")
                    .setUserId(userId)
                    .setRoomId(roomId);
            out.writeObject(request);
            out.flush();

            Message response = (Message) in.readObject();
            String data = response.getData();
            String quizArr[] = data.split("\n");
            for(int i=0;i< quizArr.length;i++){
                String tmp[] = quizArr[i].split("\t");
                quizList.add(new quiz(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]),tmp[2],tmp[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return quizList;
    }
}

class VersusStartUI {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final int roomId;
    private final String userId;
    private List<quiz> quizList;
    private int score=0;

    public VersusStartUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, int roomId, String userId, List<quiz> quizList) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.roomId = roomId;
        this.userId = userId;
        this.quizList = quizList;

        JFrame frame = new JFrame("Versus Start");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 247, 153));
        frame.add(panel);

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(500, 10, 200, 80);
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

        // 문제 표시 메서드
        displayQuiz(panel, frame, logoLabel, backButton, currentIndex);

        // 뒤로가기 버튼 동작
        backButton.addActionListener(e -> {
            frame.dispose();
            new RoomListUI(socket, out, in, "Versus Mode", userId);
        });

    }

    // 문제 표시 메서드
    private void displayQuiz(JPanel panel, JFrame frame, JLabel logoLabel, JButton backButton, int[] currentIndex) {
        panel.removeAll();

        quiz currentQuiz = quizList.get(currentIndex[0]);
        String question = currentQuiz.getQuestion();
        String answer = currentQuiz.getAnswer();

        JLabel qLabel = new JLabel((currentIndex[0] + 1) + ". " + question);
        qLabel.setBounds(175, 150, 500, 50);
        qLabel.setFont(new Font("Arial", Font.BOLD, 30));
        panel.add(qLabel);

        JLabel ansLabel = new JLabel("정답: ");
        ansLabel.setBounds(80, 320, 80, 30);
        ansLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(ansLabel);

        JTextField ansField = new JTextField();
        ansField.setBounds(130, 320, 500, 30);
        panel.add(ansField);

        JButton sendButton = new JButton("확인");
        sendButton.setBounds(660, 320, 80, 30);
        panel.add(sendButton);

        panel.add(logoLabel);
        panel.add(backButton);
        panel.revalidate();
        panel.repaint();

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userAnswer = ansField.getText();
                if (userAnswer.equals(answer)) {
                    score++;
                    JOptionPane.showMessageDialog(frame, "정답입니다! 현재 점수: " + score);
                } else {
                    JOptionPane.showMessageDialog(frame, "오답입니다. 현재 점수: " + score);
                }

                currentIndex[0]++;
                if (currentIndex[0] < quizList.size()) {
                    displayQuiz(panel, frame, logoLabel, backButton, currentIndex); // 다음 문제 호출
                } else {
                    JOptionPane.showMessageDialog(frame, "퀴즈가 종료되었습니다! 최종 점수: " + score);
                    frame.dispose();
                    new RoomListUI(socket, out, in, "Versus Mode", userId);
                }
            }
        });
    }
}

class quiz{
    private int num;
    private int second;
    private String question;
    private String answer;

    public quiz(int num, int second, String question, String answer) {
        this.num = num;
        this.second = second;
        this.question = question;
        this.answer = answer;
    }

    public int getNum() {
        return num;
    }

    public int getSecond() {
        return second;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}