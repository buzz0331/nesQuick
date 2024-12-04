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
                "id VARCHAR(15) PRIMARY KEY," +
                "password VARCHAR(15) NOT NULL," +
                "name VARCHAR(30) NOT NULL" +
                ");";

        String quizSetTable = "CREATE TABLE IF NOT EXISTS QuizSet (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id VARCHAR(15)," +
                "game_category VARCHAR(20) NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES User(id)" +
                ");";

        String roomTable = "CREATE TABLE IF NOT EXISTS Room (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "game_category VARCHAR(400) NOT NULL," +
                "master_id VARCHAR(15) NOT NULL," +
                "name VARCHAR(500)," +
                "capacity INTEGER NOT NULL," +        // 인원 제한 열
                "current_count INTEGER DEFAULT 0," +  // 방장을 제외한 방 인원
                "FOREIGN KEY(master_id) REFERENCES User(id)" +
                ");";

        String quizTable = "CREATE TABLE IF NOT EXISTS Quiz (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "quiz_set_id INTEGER NOT NULL," +
                "solved_time INTEGER," +
                "quiz VARCHAR(500) NOT NULL," +
                "answer VARCHAR(100) NOT NULL," +
                "image_url VARCHAR(1024)," +
                "FOREIGN KEY(quiz_set_id) REFERENCES QuizSet(id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(quizSetTable);
            stmt.execute(roomTable);
            stmt.execute(quizTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
