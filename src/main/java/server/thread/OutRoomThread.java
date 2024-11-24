package server.thread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OutRoomThread extends Thread{
    private final Message message;
    private final ObjectOutputStream out;

    public OutRoomThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        int roomId = Integer.parseInt(message.getData());
        String userId = message.getUserId();

        try {
            // 방에서 클라이언트 제거
            QuizServer.removeClientFromRoom(roomId, userId);

            // 방 퇴장 성공 메시지 전송
            Message response = new Message("outRoomSuccess")
                    .setData("Successfully gone out from room: " + roomId);
            out.writeObject(response);


        } catch (IOException e) {
            e.printStackTrace();
            try {
                // 방 퇴장 실패 시 실패 메시지 전송
                Message errorResponse = new Message("outRoomFailure")
                        .setData("Failed to go out room");
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
