package server.thread.speedThread;

import protocol.Message;
import server.QuizServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSpeedRankingThread extends Thread {
    private Message message;
    private ObjectOutputStream out;

    public FetchSpeedRankingThread(Message message, ObjectOutputStream out) {
        this.message = message;
        this.out = out;
    }

    @Override
    public void run() {
        int roomId = message.getRoomId();
        String userId = message.getUserId();
        int score = Integer.parseInt(message.getData());

        // 서버에 점수 등록
        QuizServer.addScore(roomId, userId, score);

        // 점수가 모두 수집될 때까지 대기
        synchronized (QuizServer.class) {
            while (!QuizServer.allScoresReceived(roomId)) {
                try {
                    QuizServer.class.wait();
                    System.out.println("wait");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 모든 점수 수집 후 랭킹 계산
        Map<String, Integer> scores = QuizServer.getScores(roomId);
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(scores.entrySet());
        ranking.sort((e1, e2) -> e2.getValue() - e1.getValue()); // 내림차순 정렬


        // 응답 메시지 생성 및 전송
        String rankingList = "";
        int curRank = 1;
        for (int i = 0; i < ranking.size(); i++) {
            Map.Entry<String, Integer> rankEntry = ranking.get(i);
            if(i>0){
                Map.Entry<String, Integer> prevEntry = ranking.get(i-1);
                if(rankEntry.getValue()<prevEntry.getValue())
                    curRank++;
            }
            rankingList += curRank+"\t"+rankEntry.getKey()+"\t"+rankEntry.getValue()+"\n";

        }
        System.out.println(rankingList);
        Message response = new Message("fetchSpeedRankingResponse")
                .setData(rankingList);
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 마지막 클라이언트가 다른 대기 중인 스레드 깨우기
        synchronized (QuizServer.class) {
            if (QuizServer.allScoresReceived(roomId)) {
                QuizServer.class.notifyAll();
            }
        }

        // 방의 점수 정보 clear할 필요 있음
        //QuizServer.clearRoomScores(roomId);
    }
}
