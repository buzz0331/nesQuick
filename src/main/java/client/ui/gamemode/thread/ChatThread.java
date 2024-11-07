package client.ui.gamemode.thread;

import protocol.Message;
import javax.swing.*;
import java.io.ObjectInputStream;

public class ChatThread extends Thread {
    private final ObjectInputStream in;
    private final JTextArea chatArea;
    private final int roomId;
    private volatile boolean running = true;

    public ChatThread(ObjectInputStream in, JTextArea chatArea, int roomId) {
        this.in = in;
        this.chatArea = chatArea;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        while (running) {
            try {
                System.out.println("hihihhi");
                Message message = (Message) in.readObject();
                if ("chat".equals(message.getType()) && message.getRoomId() == roomId) {
                    chatArea.append(message.getUserId() + ": " + message.getData() + "\n");
                }
            } catch (Exception e) {
                if (running) {  // 예외가 발생해도 스레드를 멈추지 않도록
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void stopThread() {
        running = false;
        interrupt();  // 스레드를 즉시 중단
    }
}
