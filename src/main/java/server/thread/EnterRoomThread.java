package server.thread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EnterRoomThread extends Thread {
    private final Message message;
    private final ObjectOutputStream out;
    private final Socket socket;

    public EnterRoomThread(Message message, ObjectOutputStream out, Socket socket) {
        this.message = message;
        this.out = out;
        this.socket = socket;
    }

    @Override
    public void run() {
        int roomId = Integer.parseInt(message.getData());
        String userId = message.getUserId();

        try {
            // 방에 클라이언트 추가
            QuizServer.addClientToRoom(roomId, userId, socket);

            // 방 입장 성공 메시지 전송
            Message response = new Message("enterRoomSuccess")
                    .setData("Successfully entered room: " + roomId);
            out.writeObject(response);

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
