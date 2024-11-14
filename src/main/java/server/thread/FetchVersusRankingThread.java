package server.thread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class FetchVersusRankingThread extends Thread{
    private Message message;
    private ObjectOutputStream out;

    public FetchVersusRankingThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        //String msg = message.getData();
        String data = "1\t홍길동\t3\n";
        data += "2\t김철수\t2\n";

        // 응답 메시지 생성 및 전송
        Message response = new Message("fetchVersusRankingResponse")
                .setData(data);
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
