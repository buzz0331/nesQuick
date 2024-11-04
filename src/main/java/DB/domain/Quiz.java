package DB.domain;

public class Quiz {
    private int id;
    private int quizSetId;
    private int solvedTime;
    private String quiz;
    private String answer;
    private String imageUrl;

    public Quiz(int quizSetId, String quiz, String answer) {
        this.quizSetId = quizSetId;
        this.quiz = quiz;
        this.answer = answer;
    }

    // Getters and Setters
}

