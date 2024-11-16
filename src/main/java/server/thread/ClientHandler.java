package server.thread;

import protocol.Message;
import server.thread.versusThread.FetchVersusQuizListThread;
import server.thread.versusThread.FetchVersusQuizSetsThread;
import server.thread.versusThread.FetchVersusRankingThread;
import server.thread.versusThread.SendVersusCustomQuizThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Message message = (Message) in.readObject();

                if ("register".equals(message.getType())) {
                    new RegisterThread(message, out).start();
                } else if ("login".equals(message.getType())) {
                    new LoginThread(message, out).start();
                } else if ("fetchRoomList".equals(message.getType())) {
                    new FetchRoomListThread(message, out).start();
                } else if ("createRoom".equals(message.getType())) {
                    new CreateRoomThread(message, out).start();
                } else if ("enterRoom".equals(message.getType())) {
                    new EnterRoomThread(message, out, socket).start(); // 방 입장 로직을 별도의 스레드에서 처리
                } else if ("chat".equals(message.getType())) {
                    System.out.println("채팅 수신");
                    new ChattingThread(message).start();
                } else if ("fetchVersusQuizSets".equals(message.getType())){
                    new FetchVersusQuizSetsThread(message,out).start();
                } else if ("fetchVersusQuizList".equals(message.getType())){
                    new FetchVersusQuizListThread(message,out).start();
                } else if("fetchVersusRanking".equals(message.getType())){
                    new FetchVersusRankingThread(message,out).start();
                } else if("sendVersusCustomQuiz".equals(message.getType())){
                    new SendVersusCustomQuizThread(message,out).start();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
