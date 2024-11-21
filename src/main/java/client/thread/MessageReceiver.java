package client.thread;

import protocol.Message;

import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageReceiver extends Thread {
    private final ObjectInputStream in;
    private final BlockingQueue<Message> messageQueue;
    private volatile boolean running = true;

    public MessageReceiver(ObjectInputStream in) {
        this.in = in;
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            while (running) {
                Message message = (Message) in.readObject();
                System.out.println(message.getType());
                messageQueue.put(message); // 읽은 메시지를 큐에 추가
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public Message takeMessage() throws InterruptedException {
        return messageQueue.take(); // 큐에서 메시지를 가져옴
    }

    public void stopReceiver() {
        running = false;
        interrupt();
    }
}
