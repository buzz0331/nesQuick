package client.ui.gamemode.speedQuiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartSpeedQuiz extends  JFrame {
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel scoreLabel;
    private int score = 0;

    public StartSpeedQuiz() {
        setTitle("Speed Quiz");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(255, 247, 204)); // 배경색 설정

        // 로고
        JLabel logoLabel = new JLabel(new ImageIcon("./src/main/java/client/ui/nesquick_logo.png"));
        logoLabel.setBounds(10, 10, 80, 40);
        add(logoLabel);

        // 현재 점수 표시
        scoreLabel = new JLabel("현재 스코어 : 0점");
        scoreLabel.setBounds(300, 10, 100, 30);
        add(scoreLabel);

        // 문제 표시
        questionLabel = new JLabel("Q1. 사과를 한 입 베어 먹으면?");
        questionLabel.setBounds(50, 80, 300, 30);
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.setOpaque(true);
        questionLabel.setBackground(new Color(255, 255, 204));
        add(questionLabel);

        // 답 입력 필드
        answerField = new JTextField();
        answerField.setBounds(120, 150, 150, 30);
        add(answerField);

        // 정답 버튼
        JButton submitButton = new JButton("정답");
        submitButton.setBounds(150, 200, 80, 30);
        submitButton.setBackground(new Color(255, 223, 85));
        add(submitButton);

        // 정답 버튼 클릭 시 동작
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(answerField.getText().trim());
            }
        });

        setVisible(true);
    }

    // 정답 체크
    private void checkAnswer(String answer) {
        if (answer.equals("사과")) { // 예시 정답
            score += 10;
            JOptionPane.showMessageDialog(this, "정답입니다!");
            scoreLabel.setText("현재 스코어 : " + score + "점");
            // 다음 문제로 이동하는 로직 추가 가능
        } else {
            JOptionPane.showMessageDialog(this, "오답입니다. 다시 시도하세요!");
        }
        answerField.setText("");
    }
}