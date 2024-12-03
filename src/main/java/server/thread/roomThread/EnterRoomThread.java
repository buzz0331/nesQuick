package server.thread.roomThread;

import protocol.Message;
import server.QuizServer;
import server.StoreStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        try (Connection conn = QuizServer.getConnection()) {
            // 방 정보 가져오기
            String query = "SELECT capacity, current_count, master_id FROM Room WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int capacity = rs.getInt("capacity");
                int currentCount = rs.getInt("current_count");
                String masterId = rs.getString("master_id");

                // 방장이 아닌 경우 capacity 초과 확인
                if (!userId.equals(masterId) && currentCount >= capacity) {
                    // 방 인원 초과 메시지 전송
                    Message errorResponse = new Message("enterRoomFailure")
                            .setData("방 인원 초과!");
                    out.writeObject(errorResponse);
                    return;
                }

                // 방에 클라이언트 추가
                QuizServer.addClientToRoom(roomId, userId, storeStream);

                // current_count 증가
                String updateQuery = "UPDATE Room SET current_count = current_count + 1 WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, roomId);
                updateStmt.executeUpdate();

                // 방 입장 성공 메시지 전송
                Message response = new Message("enterRoomSuccess")
                        .setRoomId(roomId)
                        .setUserId(userId)
                        .setData(String.valueOf(QuizServer.getRoomUserCount(roomId)))
                        .setRoomMaster(masterId);
                out.writeObject(response);
                out.flush();

                // 다른 사용자에게 입장 알림 메시지 브로드캐스트
                Message broadcastMessage = new Message("userEnter")
                        .setRoomId(roomId)
                        .setUserId(userId)
                        .setData("플레이어 " + userId + " 님이 입장하셨습니다.");
                QuizServer.broadcast(roomId, broadcastMessage);
            } else {
                // 방 정보가 없는 경우
                Message errorResponse = new Message("enterRoomFailure")
                        .setData("방 정보를 찾을 수 없습니다.");
                out.writeObject(errorResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 방 입장 실패 메시지 전송
                Message errorResponse = new Message("enterRoomFailure")
                        .setData("Failed to enter room");
                out.writeObject(errorResponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
