package server.handler;

import protocol.Message;
import server.StoreStream;
import server.thread.versusThread.FetchVersusQuizListThread;
import server.thread.versusThread.FetchVersusQuizSetsThread;
import server.thread.versusThread.FetchVersusRankingThread;
import server.thread.versusThread.SendVersusCustomQuizThread;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class VersusHandler implements MessageHandler {
    @Override
    public void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException {
        switch (message.getType()) {
            case "fetchVersusQuizSets":
                new FetchVersusQuizSetsThread(message, out).start();
                break;
            case "fetchVersusQuizList":
                new FetchVersusQuizListThread(message, out).start();
                break;
            case "fetchVersusRanking":
                new FetchVersusRankingThread(message, out).start();
                break;
            case "sendVersusCustomQuiz":
                new SendVersusCustomQuizThread(message, out).start();
                break;
            default:
                System.out.println("Unknown Versus Message Type: " + message.getType());
        }
    }
}

