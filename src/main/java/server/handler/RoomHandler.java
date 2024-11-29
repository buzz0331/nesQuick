package server.handler;

import protocol.Message;
import server.StoreStream;
import server.thread.roomThread.CreateRoomThread;
import server.thread.roomThread.EnterRoomThread;
import server.thread.roomThread.FetchRoomListThread;
import server.thread.roomThread.OutRoomThread;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class RoomHandler implements MessageHandler {
    @Override
    public void handle(Message message, ObjectOutputStream out, StoreStream storeStream) throws IOException {
        switch (message.getType()) {
            case "fetchRoomList":
                new FetchRoomListThread(message, out).start();
                break;
            case "createRoom":
                new CreateRoomThread(message, out).start();
                break;
            case "enterRoom":
                new EnterRoomThread(message, out, storeStream).start();
                break;
            case "outRoom":
                new OutRoomThread(message, out).start();
                break;
            default:
                System.out.println("Unknown Room Message Type: " + message.getType());
        }
    }
}

