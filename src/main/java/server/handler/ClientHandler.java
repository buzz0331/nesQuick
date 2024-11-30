package server.handler;

import protocol.Message;
import server.StoreStream;
import server.handler.SpeedHandler;
import server.handler.VersusHandler;
import server.handler.CooperationHandler;
import server.thread.ChattingThread;
import server.thread.LoginThread;
import server.thread.RegisterThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final StoreStream storeStream;

    private final Map<String, MessageHandler> handlers;

    public ClientHandler(StoreStream storeStream) {
        this.in = storeStream.getIn();
        this.out = storeStream.getOut();
        this.storeStream = storeStream;

        // 각 메시지 타입에 대한 핸들러를 매핑
        handlers = new HashMap<String, MessageHandler>();
        handlers.put("Room", new RoomHandler());
        handlers.put("Speed", new SpeedHandler());
        handlers.put("Versus", new VersusHandler());
        handlers.put("Cooperation", new CooperationHandler());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) in.readObject();

                // 메시지 타입에 따른 핸들러 호출
                String handlerKey = getHandlerKey(message.getType());
                MessageHandler handler = handlers.get(handlerKey);

                if (handler != null) {
                    handler.handle(message, out, storeStream);
                } else {
                    // 나머지 로직은 ClientHandler에서 처리
                    handleOtherMessages(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getHandlerKey(String type) {
        if (type.contains("Room")) return "Room";
        if (type.contains("Speed")) return "Speed";
        if (type.contains("Versus")) return "Versus";
        if (type.contains("Cooperation")) return "Cooperation";
        return null;
    }

    private void handleOtherMessages(Message message) {
        try {
            switch (message.getType()) {
                case "register":
                    new RegisterThread(message, out).start();
                    break;
                case "login":
                    new LoginThread(message, out).start();
                    break;
                case "chat":
                    System.out.println("채팅 수신");
                    new ChattingThread(message).start();
                    break;
                default:
                    System.out.println("Unknown message type: " + message.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
