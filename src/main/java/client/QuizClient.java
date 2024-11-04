package client;

import client.ui.InitialUI;

import java.io.*;
import java.net.*;

public class QuizClient {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // 초기 화면을 보여줌
            new InitialUI(socket, out, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
