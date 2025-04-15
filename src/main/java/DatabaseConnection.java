import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Connection String
    private static final String URL = "jdbc:h2:mem:";
    private static Connection connection;

    private DatabaseConnection(){}

    public static Connection getConnection(){
        if (connection == null) {
            // connection is NOT set up already, we have to establish one
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Connection established successfully");
            } catch (SQLException e) {
                throw new RuntimeException("Connection failed", e);
            }
        }
        return connection;
    }
}
