package server.thread.versusThread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class FetchVersusQuizSetsThread extends Thread{
    private Message message;
    private ObjectOutputStream out;

    public FetchVersusQuizSetsThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        //String msg = message.getData();

        String data = "건국대문제\n";
        data += "sample\n";

        // 응답 메시지 생성 및 전송
        Message response = new Message("fetchVersusQuizSetsResponse")
                .setData(data);
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
