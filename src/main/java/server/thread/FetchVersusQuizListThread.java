package server.thread;

import protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
public class FetchVersusQuizListThread extends Thread{
    private Message message;
    private ObjectOutputStream out;

    public FetchVersusQuizListThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        String msg = message.getData();

        // msg에 저장된 퀴즈 set의 이름에 해당하는 퀴즈 리스트 전송

        String data = "1\t10\t일감호에 있는 다리 이름은?\t홍예교\n";
        data += "2\t10\t건대 마스코트 거위 이름은?\t건구스\n";
        data += "3\t10\t건대 공학관 건물 번호는?\t21\n";

        // 응답 메시지 생성 및 전송
        Message response = new Message("fetchVersusQuizListResponse")
                .setData(data);
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
