package DAO;

import Models.Meeting;

import java.util.HashMap;

public interface IMeetingDAO {

    void InsertMeeting(Meeting m);
    int deleteSingleMeeting(Meeting m);
    int deleteMeetingSeries(Meeting m);
    int updateSingleMeeting(Meeting m);
    int updateMeetingSeries(Meeting m);
    Meeting getMeeting(String name);
    HashMap<Meeting, Integer> getMeetingOccurances(Meeting m, String start, String end);


}
