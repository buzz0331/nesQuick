package server;

import protocol.Message;
import server.thread.ClientHandler;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizServer {
    private static final int PORT = 12345;
//    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
private static final Map<Integer, Map<String, Socket>> rooms = new HashMap<Integer, Map<String, Socket>>();
    private static final Map<Integer, Map<String, Integer>> roomScores = new HashMap<>(); // 방 별 사용자 점수 저장

    public static synchronized void addClientToRoom(int roomId, String userId, Socket clientSocket) {
        rooms.computeIfAbsent(roomId, k -> new HashMap<>()).put(userId, clientSocket);
    }

    public static synchronized void removeClientFromRoom(int roomId, String userId) {
        Map<String, Socket> room = rooms.get(roomId);
        if (room != null) {
            Socket clientSocket = room.remove(userId);  // userId로 소켓을 찾고 제거
            if (clientSocket != null) {
                try {
                    clientSocket.close();  // 소켓 연결을 종료
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized void broadcast(int roomId, Message message) {
        Map<String, Socket> room = rooms.get(roomId);
        if (room != null) {
            for (Map.Entry<String, Socket> entry : room.entrySet()) {
                String userId = entry.getKey();
                Socket clientSocket = entry.getValue();

                // 메시지를 전송한 사용자 자신에게는 전송하지 않음
                if (!userId.equals(message.getUserId())) {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(message);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
//                clients.add(clientHandler);
                new Thread(clientHandler).start();

                //client 종료시에 List에서 제거
//                clients.remove(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static synchronized void clearRoomScores(int roomId) {
        roomScores.remove(roomId);
    }

}
