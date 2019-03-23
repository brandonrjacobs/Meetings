package stdin;

import DAO.SimpleMeetingDAO;
import Models.SimpleMeeting;

public class DatabaseConnectionTest {

    public static void main(String[] args){

        SimpleMeetingDAO dao = new SimpleMeetingDAO();
        SimpleMeeting m = new SimpleMeeting();
        m.setDayOfWeek("Mon");
        m.setStartDate("2019-06-01");
        m.setEndDate("2020-06-02");
        System.out.println("Checking if database contains new meeting--Assert = False --> " + dao.containsMeeting(m));

    }
}
