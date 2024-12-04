package server.handler;

import protocol.Message;
import server.QuizServer;
import server.StoreStream;
import server.thread.cooperationThread.FetchCooperationQuizListThread;
import server.thread.cooperationThread.FetchCooperationQuizSetsThread;
import server.thread.cooperationThread.SendCooperationCustomQuizThread;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class CooperationHandler implements MessageHandler {
    @Override
    public void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException {
        switch (message.getType()) {
            case "fetchCooperationQuizSets":
                new FetchCooperationQuizSetsThread(message, out).start();
                break;
            case "fetchCooperationQuizList":
                new FetchCooperationQuizListThread(message, out).start();
                break;
            case "sendCooperationCustomQuiz":
                new SendCooperationCustomQuizThread(message, out).start();
                break;
            case "nextCooperationQuiz":
                Message response = new Message("toNextCooperationQuiz")
                        .setRoomId(message.getRoomId())
                        .setData(message.getData());
                QuizServer.broadcastToAll(message.getRoomId(), response);
                break;
            default:
                System.out.println("Unknown Cooperation Message Type: " + message.getType());
        }
    }
}

