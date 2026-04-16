package edu.ijse.mvc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection dbConnection;
    private Connection connection;

    // private constructor (Singleton)
    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/your_database_name",
                "root",
                "mysql"
        );
    }

    // get single instance
    public static DBConnection getInstance()
            throws ClassNotFoundException, SQLException {

        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    // getter for connection
    public Connection getConnection() {
        return connection;
    }
}
