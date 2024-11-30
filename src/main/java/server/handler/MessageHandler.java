package server.handler;

import protocol.Message;
import server.StoreStream;

import java.io.IOException;
import java.io.ObjectOutputStream;

public interface MessageHandler {
    void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException;
}

