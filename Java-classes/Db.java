
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/webp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"; // Change to your DB URL
    private static final String DB_USER = "root"; // Change to your DB username
    private static final String DB_PASSWORD = "admin"; // Change to your DB password

    static {
        try {
            // Load the MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MariaDB JDBC driver.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}