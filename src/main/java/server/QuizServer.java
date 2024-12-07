package server;

import protocol.Message;
import server.handler.ClientHandler;

import java.io.*;
import java.net.*;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;

public class QuizServer {
    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
private static final Map<Integer, Map<String, StoreStream>> rooms = new HashMap<Integer, Map<String, StoreStream>>();
    // 랭킹 처리를 위해 추가한 Map입니다
    private static final Map<Integer, Map<String, Integer>> roomScores = new HashMap<>(); // 방 별 사용자 점수 저장

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static synchronized void addClientToRoom(int roomId, String userId, StoreStream storeStream) {
        rooms.computeIfAbsent(roomId, k -> new HashMap<>()).put(userId, storeStream);
    }

    public static synchronized void removeClientFromRoom(int roomId, String userId) {
        Map<String, StoreStream> room = rooms.get(roomId);
        if (room != null) {
            room.remove(userId);
            if (room.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    public static synchronized void broadcast(int roomId, Message message) {
        Map<String, StoreStream> room = rooms.get(roomId);
        if (room != null) {
            for (Map.Entry<String, StoreStream> entry : room.entrySet()) {
                String userId = entry.getKey();
                StoreStream storeStream = entry.getValue();

                // 메시지를 전송한 사용자 자신에게는 전송하지 않음
                if (!userId.equals(message.getUserId())) {
                    try {
                        ObjectOutputStream out = storeStream.getOut();
                        out.writeObject(message);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //스피드퀴즈에서 다음 문제 넘어갈 때 사용하기 위해 작성
    public static synchronized void broadcastToAll(int roomId, Message message) {
        Map<String, StoreStream> room = rooms.get(roomId);
        if (room != null) {
            for (Map.Entry<String, StoreStream> entry : room.entrySet()) {
                StoreStream storeStream = entry.getValue();

                try {
                    ObjectOutputStream out = storeStream.getOut();
                    out.writeObject(message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) throws IOException {
        Socket clientSocket = null;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is running...");
            while (true) {
                clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                StoreStream socket = new StoreStream(in, out);
                ClientHandler clientHandler = new ClientHandler(socket);
//                clients.add(clientHandler);
                new Thread(clientHandler).start();

                //client 종료시에 List에서 제거
//                clients.remove(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 대전 모드에서 랭킹 처리를 위해 작성한 메소드들입니다.
    public static synchronized void addScore(int roomId, String userId, int score) {
        roomScores.computeIfAbsent(roomId, k -> new HashMap<>()).put(userId, score);
    }

    public static synchronized boolean allScoresReceived(int roomId) {
        return roomScores.get(roomId).size() == rooms.get(roomId).size();
    }

    public static synchronized Map<String, Integer> getScores(int roomId) {
        return new HashMap<>(roomScores.getOrDefault(roomId, new HashMap<>()));
    }

    public static synchronized int getRoomUserCount(int roomId) {
        Map<String, StoreStream> room = rooms.get(roomId);
        return (room != null) ? room.size() : 0;
    }

}
