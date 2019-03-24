package DAO;

import Models.*;
import java.util.List;
import java.util.HashMap;

public interface ISimpleMeetingDAO {


    void insertMeeting(SimpleMeeting m);
    int[] insertMeetingList(List<SimpleMeeting> meetings);
    SimpleMeeting getMeeting(int id);
    int getMeetingCount(String day);
    HashMap<String,Integer> getMeetingsInDateRange(String start, String end);
    int updateMeetingDay(String s_date, String e_date, String origDay, String newDay);
    int updateMeetingDates(int id, String new_start, String new_end);

    int deleteMeeting(int id);

    int insertException(String date, String dow);


}
