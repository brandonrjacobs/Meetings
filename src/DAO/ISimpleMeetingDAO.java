package DAO;

import Models.*;
import java.util.List;

public interface ISimpleMeetingDAO {


    void insertMeeting(SimpleMeeting m);
    int[] insertMeetingList(List<SimpleMeeting> meetings);
    SimpleMeeting getMeeting(int id);
    int getMeetingCount(String day);
    int updateMeetingDay(String s_date, String e_date, String origDay, String newDay);
    int updateMeetingDates(int id, String new_start, String new_end);

    int deleteMeeting(int id);

    int insertException(String date, String dow);


}
