import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {

        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

    public static String getIdByNicknameFromMain(String nick) {
        String sql = String.format("SELECT id FROM main where nickname = '%s'", nick);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIdByNick1AndNick2FromBlocklist(String who, String whom) {
        String sql = String.format("SELECT id FROM blocklist WHERE who='%s' and whom='%s'", who, whom);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
