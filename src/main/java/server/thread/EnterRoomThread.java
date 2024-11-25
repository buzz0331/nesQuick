package server.thread;

import DB.domain.Quiz;
import protocol.Message;
import server.QuizServer;
import server.StoreStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EnterRoomThread extends Thread {
    private final Message message;
    private final ObjectOutputStream out;
    private final StoreStream storeStream;

    public EnterRoomThread(Message message, ObjectOutputStream out, StoreStream storeStream) {
        this.message = message;
        this.out = out;
        this.storeStream = storeStream;
    }

    @Override
    public void run() {
        int roomId = Integer.parseInt(message.getData());
        String userId = message.getUserId();

        try {
            //방장 정보 알아오기(들어가는 유저가 방장인지 아닌지 비교)
            String masterId = QuizServer.getRoomMaster(roomId);
            // 방에 클라이언트 추가
            QuizServer.addClientToRoom(roomId, userId, storeStream);

            System.out.println(message.getRoomMaster());
            // 방 입장 성공 메시지 전송
            Message response = new Message("enterRoomSuccess")
                    .setRoomId(roomId)
                    .setUserId(userId)
                    .setData(String.valueOf(QuizServer.getRoomUserCount(roomId)))
                    .setRoomMaster(masterId);
            out.writeObject(response);
            out.flush();
            System.out.println("EnterRoomThread.run: "+ userId);

            // 다른 사용자에게 입장 알림 메시지 브로드캐스트
            Message broadcastMessage = new Message("userEnter")
                    .setRoomId(roomId)
                    .setUserId(userId)
                    .setData("플레이어 " + userId + " 님이 입장하셨습니다.");
            System.out.println("EnterRoomThread.run"+ userId);
            QuizServer.broadcast(roomId, broadcastMessage);

        } catch (IOException e) {
            e.printStackTrace();
            try {
                // 방 입장 실패 시 실패 메시지 전송
                Message errorResponse = new Message("enterRoomFailure")
                        .setData("Failed to enter room");
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}