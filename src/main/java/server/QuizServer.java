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
    private static final Map<String, ObjectOutputStream> userOutputStreams = new HashMap<>();

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
        userOutputStreams.remove(userId);
    }

    //해당 유저의 ObjectOutputStream을 반환하거나, 없으면 생성하여 반환
    public static synchronized ObjectOutputStream getOrCreateOutputStream(String userId, Socket clientSocket) {
        ObjectOutputStream out = userOutputStreams.get(userId);
        if(out == null) {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userOutputStreams.put(userId, out);
        }
        return out;
    }

    //방 안에 있는 유저 정보(유저들의 ID)를 받아오기 위함
    public static synchronized List<String> getUserIdsInRoom(int roomId) {
        Map<String, Socket> room = rooms.get(roomId);
        if(room != null) {
            return new ArrayList<>(room.keySet()); //room에 있는 유저들의 ID목록을 리스트로 반환
        }
        return new ArrayList<>();  //방이 없으면 빈 리스트 반환(근데 애초에 방 없을 때 요청 못하도록)
    }

    //GameStartThread에서 유저들의 각 ID를 받아 해당 유저 Socket의 OutputStream 반환
    public static synchronized ObjectOutputStream getClientOutputStream(String userId) {
        for(Map<String,Socket> room : rooms.values()) {
            if(room.containsKey(userId)) {
                return getOrCreateOutputStream(userId, room.get(userId));
            }
        }
        return null; //유저가 없으면 null 반환
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
                        //ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectOutputStream out = getOrCreateOutputStream(userId, clientSocket);
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

}
