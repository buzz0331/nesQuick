package client.ui.gamemode.versusUI;

public class Quiz{
    private int num;
    private int second;
    private String question;
    private String answer;

    public Quiz(int num, int second, String question, String answer) {
        this.num = num;
        this.second = second;
        this.question = question;
        this.answer = answer;
    }
    public int getNum() {
        return num;
    }

    public int getSecond() {
        return second;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
