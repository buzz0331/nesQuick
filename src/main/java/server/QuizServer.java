package server;

import server.protocol.Message;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizServer {
    private static final int PORT = 12345;
//    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    static class ClientHandler implements Runnable {
//        private Socket socket;
//        private ObjectOutputStream out;
//        private ObjectInputStream in;
//
//        public ClientHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            try {
//                out = new ObjectOutputStream(socket.getOutputStream());
//                in = new ObjectInputStream(socket.getInputStream());
//
//                while (true) {
//                    Message message = (Message) in.readObject();
//
//                    if (message.getType().equals("register")) {
//                        registerUser(message);
//                    } else if (message.getType().equals("login")) {
//                        loginUser(message);
//                    }
//
//                    out.writeObject(message); // 결과를 클라이언트에 전송
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        private void registerUser(Message message) {
//            try (Connection conn = DriverManager.getConnection(DB_URL);
//                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO User (id, name, password) VALUES (?, ?, ?)")) {
//                stmt.setString(1, message.getUserId());
//                stmt.setString(2, message.getUsername());
//                stmt.setString(3, message.getPassword());
//                stmt.executeUpdate();
//                message.setData("Registration successful");
//            } catch (SQLException e) {
//                e.printStackTrace();
//                message.setData("Registration failed");
//            }
//        }
//
//        private void loginUser(Message message) {
//            try (Connection conn = DriverManager.getConnection(DB_URL);
//                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE id = ? AND password = ?")) {
//                System.out.println("UserId() = " + message.getUserId());
//                System.out.println("Password() = " + message.getPassword());
//                stmt.setString(1, message.getUserId());
//                stmt.setString(2, message.getPassword());
//                ResultSet rs = stmt.executeQuery();
//                if (rs.next()) {
//                    message.setData("Login successful");
//                } else {
//                    message.setData("Invalid credentials");
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//                message.setData("Login failed");
//            }
//        }
//    }
}
