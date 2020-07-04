import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

// Данный класс в проекте устарел и не используется.
// Существует только в качестве наглядного примера альтернативы Hibernate
// Чтобы использовать данный класс необходимо его раскомментировать в классах ClientHandler и ServerMain,
// и закомментировать классы Hibernate
public class MainSQLiteDB {
    private static Connection connection;
    private static Statement stmt;
    static final Logger rootLogger = LogManager.getRootLogger();

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            rootLogger.error(e.getMessage());
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        try {
            String sql = "SELECT nickname FROM main where login = ? and password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);


            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            rootLogger.error(e.getMessage());
        }
        return null;
    }

    public static String insertTable(String who, String whom) {
        String sql = String.format("INSERT INTO blocklist(who, whom) VALUES('%s', '%s')", who, whom);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            rootLogger.error(e.getMessage());
        }
        return null;
    }

    public static String getIdByNicknameFromMain(String nick) {
        try {
            String sql = "SELECT id FROM main where nickname = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nick);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            rootLogger.error(e.getMessage());
        }
        return null;
    }

    public static String getIdByNick1AndNick2FromBlocklist(String who, String whom) {
        try {
            String sql = "SELECT id FROM blocklist WHERE who=? and whom=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, who);
            preparedStatement.setString(2, whom);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            rootLogger.error(e.getMessage());
        }
        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            rootLogger.error(e.getMessage());
        }
    }
}
