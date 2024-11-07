package server.thread;

import protocol.Message;
import server.QuizServer;

import java.io.ObjectOutputStream;
import java.io.IOException;

public class ChattingThread extends Thread {
    private final Message message;

    public ChattingThread(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        int roomId = message.getRoomId();
        String userId = message.getUserId();
        String chatContent = message.getData();

        // 동일 방의 다른 클라이언트에게 메시지 브로드캐스트
        Message broadcastMessage = new Message("chat")
                .setRoomId(roomId)
                .setUserId(userId)
                .setData(chatContent);
        System.out.println("SbroadcastMessage.getData() = " + broadcastMessage.getData());
        System.out.println("SbroadcastMessage.getUserId() = " + broadcastMessage.getUserId());
        System.out.println("SbroadcastMessage.getRoomId() = " + broadcastMessage.getRoomId());
        QuizServer.broadcast(roomId, broadcastMessage);
    }
}
