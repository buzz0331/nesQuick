package client.ui.gamemode.cooperationUI;

public class Quiz{
    private int num;
    private int time;
    private String question;
    private String answer;

    public Quiz(int num, int time, String question, String answer) {
        this.num = num;
        this.time = time;
        this.question = question;
        this.answer = answer;
    }
    public int getNum() {
        return num;
    }

    public int getTime() {
        return time;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
