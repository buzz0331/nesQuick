package server.handler;

import protocol.Message;
import server.QuizServer;
import server.StoreStream;
import server.thread.speedThread.FetchSpeedQuizListThread;
import server.thread.speedThread.FetchSpeedQuizSetsThread;
import server.thread.speedThread.FetchSpeedRankingThread;
import server.thread.speedThread.SendSpeedCustomQuizThread;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SpeedHandler implements MessageHandler {
    @Override
    public void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException {
        switch (message.getType()) {
            case "fetchSpeedQuizSets":
                new FetchSpeedQuizSetsThread(message, out).start();
                break;
            case "fetchSpeedQuizList":
                new FetchSpeedQuizListThread(message, out).start();
                break;
            case "fetchSpeedRanking":
                new FetchSpeedRankingThread(message, out).start();
                break;
            case "sendSpeedCustomQuiz":
                new SendSpeedCustomQuizThread(message, out).start();
                break;
            case "nextSpeedQuiz":
                Message response = new Message("toNextSpeedQuiz")
                        .setRoomId(message.getRoomId())
                        .setData(message.getData());
                QuizServer.broadcastToAll(message.getRoomId(), response);
                break;
            default:
                System.out.println("Unknown Speed Message Type: " + message.getType());
        }
    }
}

