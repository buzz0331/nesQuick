package server.thread.roomThread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OutRoomThread extends Thread {
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
        String masterId = message.getRoomMaster();

        try (Connection conn = QuizServer.getConnection()) {
            // 방에서 클라이언트 제거
            QuizServer.removeClientFromRoom(roomId, userId);

            if (masterId == null) { //게임이 끝나면 null로 넘어옴
                String updateQuery = "UPDATE Room SET current_count = 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();
            }
            else if (!userId.equals(masterId)) { //방장이 아닌 경우
                // current_count 감소
                String updateQuery = "UPDATE Room SET current_count = current_count - 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();
            }

//            // 방 퇴장 성공 메시지 전송
            Message response = new Message("outRoomSuccess")
                    .setData("Successfully gone out from room: " + roomId);
            out.writeObject(response);

            if (masterId != null) {
                // 다른 사용자에게 퇴장 알림 메시지 브로드캐스트
                Message broadcastMessage = new Message("userExit")
                        .setRoomId(roomId)
                        .setUserId(userId)
                        .setData("플레이어 " + userId + " 님이 퇴장하셨습니다.");
                QuizServer.broadcast(roomId, broadcastMessage);
            }


        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 방 퇴장 실패 메시지 전송
                Message errorResponse = new Message("outRoomFailure")
                        .setData("Failed to go out room");
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
