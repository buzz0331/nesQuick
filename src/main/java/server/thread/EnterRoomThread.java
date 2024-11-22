package server.thread;

import protocol.Message;
import server.QuizServer;
import server.StoreStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;

public class EnterRoomThread extends Thread {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
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

        try(Connection conn = DriverManager.getConnection(DB_URL)){
            // 방에 클라이언트 추가
            QuizServer.addClientToRoom(roomId, userId, storeStream);

            String query = "SELECT name, capacity, master_id FROM Room WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // 방 입장 성공 시 방 정보로 응답 메시지 구성
                    Message response = new Message("enterRoomSuccess")
                            .setData(String.valueOf(QuizServer.getRoomUserCount(roomId)))
                            .setRoomId(roomId)
                            .setRoomName(rs.getString("name"))
                            .setCapacity(rs.getInt("capacity"))
                            .setRoomMaster(rs.getString("master_id"));
                    out.writeObject(response);
                } else {
                    // 방을 찾을 수 없는 경우
                    Message errorResponse = new Message("enterRoomFailure")
                            .setData("Room not found");
                    out.writeObject(errorResponse);
                }
            }

            // 다른 사용자에게 입장 알림 메시지 브로드캐스트
            Message broadcastMessage = new Message("userEnter")
                    .setRoomId(roomId)
                    .setUserId(userId)
                    .setData("플레이어 " + userId + " 님이 입장하셨습니다.");
            System.out.println("EnterRoomThread.run"+ userId);
            QuizServer.broadcast(roomId, broadcastMessage);


        } catch (IOException | SQLException e) {
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
