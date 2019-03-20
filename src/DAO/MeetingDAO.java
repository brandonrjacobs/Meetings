package DAO;
import java.sql.*;
import java.util.*;

public class MeetingDAO {
    private static final String URL = "jdbc:postgresql://localhost/meeting_application";

    public MeetingDAO(){

    }

    public void createMeeting(Meeting m) throws SQLException{
        String url = "jdbc:postgresql://localhost/test";
        Properties props = new Properties();
        props.setProperty("user","fred");
        props.setProperty("password","secret");
        props.setProperty("ssl","true");
        Connection conn = DriverManager.getConnection(url, props);

        url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
        conn = DriverManager.getConnection(url);
    }


}
