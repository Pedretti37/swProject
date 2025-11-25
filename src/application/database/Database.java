package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	private static final String URL = "jdbc:mysql://localhost:3306/glicocare";
	private static final String USER = "root";
	private static final String PASSWORD = "password";

    private Database() {} //costruttore privato
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}