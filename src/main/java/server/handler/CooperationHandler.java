package server.handler;

import protocol.Message;
import server.StoreStream;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class CooperationHandler implements MessageHandler {
    @Override
    public void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException {
        // Cooperation 관련 로직 추가
        System.out.println("Handling Cooperation Message: " + message.getType());
    }
}

