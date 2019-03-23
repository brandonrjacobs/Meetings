package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:postgresql://localhost:5432/meeting_application";
    private static final String USER = "brandon";
    private static final String PASSWORD = "";

    public static Connection getConnection(){

        try {

            return DriverManager.getConnection(URL, USER, PASSWORD);

        }catch(SQLException e){

            throw new RuntimeException("Error Connecting to Postgres Database", e);
        }
    }

}
