package server.thread;

import protocol.Message;
import server.QuizServer;
import server.StoreStream;
import server.thread.speedThread.FetchSpeedQuizListThread;
import server.thread.speedThread.FetchSpeedQuizSetsThread;
import server.thread.speedThread.FetchSpeedRankingThread;
import server.thread.speedThread.SendSpeedCustomQuizThread;
import server.thread.versusThread.FetchVersusQuizListThread;
import server.thread.versusThread.FetchVersusQuizSetsThread;
import server.thread.versusThread.FetchVersusRankingThread;
import server.thread.versusThread.SendVersusCustomQuizThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private StoreStream storeStream;

    public ClientHandler(StoreStream storeStream) {
        this.in = storeStream.getIn();
        this.out = storeStream.getOut();
        this.storeStream = storeStream;
    }

    @Override
    public void run() {
        try {
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());

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
                    new EnterRoomThread(message, out, storeStream).start(); // 방 입장 로직을 별도의 스레드에서 처리
                } else if("outRoom".equals(message.getType())){
                    new OutRoomThread(message,out).start();
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
                }  else if ("fetchSpeedQuizSets".equals(message.getType())){
                    new FetchSpeedQuizSetsThread(message,out).start();
                } else if ("fetchSpeedQuizList".equals(message.getType())){
                    new FetchSpeedQuizListThread(message,out).start();
                } else if("fetchSpeedRanking".equals(message.getType())){
                    new FetchSpeedRankingThread(message,out).start();
                } else if("sendSpeedCustomQuiz".equals(message.getType())){
                    new SendSpeedCustomQuizThread(message,out).start();
                } else if("nextSpeedQuiz".equals(message.getType())){
                    Message response = new Message("toNextSpeedQuiz")
                            .setRoomId(message.getRoomId())
                            .setData(message.getData());
                    QuizServer.broadcastToAll(message.getRoomId(), response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
