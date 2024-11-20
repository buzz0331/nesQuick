package client.ui.gamemode.versusUI;

import client.thread.MessageReceiver;
import protocol.Message;

public class VersusStartHandler extends Thread{
    private MessageReceiver receiver;

    public VersusStartHandler(MessageReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            Message startMessage = receiver.takeMessage();
            System.out.println(startMessage.getData());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
