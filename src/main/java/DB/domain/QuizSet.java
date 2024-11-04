package DB.domain;

public class QuizSet {
    private int id;
    private int gameCategoryId;
    private int userId;
    private int recommendedCount;

    public QuizSet(int gameCategoryId, int userId) {
        this.gameCategoryId = gameCategoryId;
        this.userId = userId;
        this.recommendedCount = 0;
    }

    // Getters and Setters
}

