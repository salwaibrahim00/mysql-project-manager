package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
    // Database connection details
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "projects";
    private static final String USERNAME = "student";
    private static final String PASSWORD = "student";
    
    // Build the connection URL
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + 
                                     "?user=" + USERNAME + "&password=" + PASSWORD + "&useSSL=false";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("✓ Connected to database successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("✗ Failed to connect to database!");
            throw new DbException("Cannot connect to database: " + e.getMessage());
        }
    }
}