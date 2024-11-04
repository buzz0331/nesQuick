package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInit {
    private static final String DB_URL = "jdbc:sqlite:quiz_game.db";

    public static void main(String[] args) {
        createTables();
    }

    public static void createTables() {
        String userTable = "CREATE TABLE IF NOT EXISTS User (" +
                "id TEXT PRIMARY KEY," +       // 사용자 직접 입력
                "password TEXT NOT NULL," +
                "win_count INTEGER DEFAULT 0," +
                "name TEXT NOT NULL" +
                ");";

        String quizSetTable = "CREATE TABLE IF NOT EXISTS QuizSet (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "game_category_id INTEGER NOT NULL," +
                "user_id TEXT NOT NULL," +
                "recommended_count INTEGER DEFAULT 0," +
                "FOREIGN KEY(user_id) REFERENCES User(id)" +
                ");";

        String gameCategoryTable = "CREATE TABLE IF NOT EXISTS GameCategory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL" +
                ");";

        String quizTable = "CREATE TABLE IF NOT EXISTS Quiz (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_set_id INTEGER NOT NULL," +
                "solved_time INTEGER," +
                "quiz TEXT NOT NULL," +
                "answer TEXT NOT NULL," +
                "image_url TEXT," +
                "FOREIGN KEY(quiz_set_id) REFERENCES QuizSet(id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(quizSetTable);
            stmt.execute(gameCategoryTable);
            stmt.execute(quizTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
