package server.thread.versusThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendVersusCustomQuizThread extends Thread{
    private Message message;
    private ObjectOutputStream out;

    public SendVersusCustomQuizThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String msg = message.getData();

        // DB에 저장

        System.out.println(msg);

        // 응답 메시지 생성 및 전송
        Message response = new Message("sendVerSusCustomQuizResponse")
                .setData("ok");
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
