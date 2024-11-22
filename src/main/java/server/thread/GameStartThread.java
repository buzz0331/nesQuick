package server.thread;

import DB.domain.User;
import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class GameStartThread extends Thread{
    private Message message;
    private ObjectOutputStream out;
    private List<String> userIds;

    public GameStartThread(Message message, ObjectOutputStream out,List<String> userIds ) {
        this.message = message;
        this.out = out;
        this.userIds = userIds;
    }

    @Override
    public void run() {
        System.out.println("Game Start");
        Message response = new Message("startGameSuccess")
                .setData("Game Started Successfully");

//        for(String userId : userIds) {
//            ObjectOutputStream userOut = QuizServer.getClientOutputStream(userId);
//            System.out.println(userId+ " 추가" + userOut);
//            if(userOut != null) {
//                try {
//                    userOut.writeObject(response);
//                    System.out.println(response);
//                    userOut.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("유저" + userId + "의 출력 스트림을 찾을 수 없음");
//            }
//        }
    }
}

