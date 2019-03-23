package DAO;

import Models.SimpleMeeting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
public class SimpleMeetingDAO implements ISimpleMeetingDAO{
    private Connection conn;
    private HashSet<String> uniqueMeetings;

    public SimpleMeetingDAO(Connection conn) {
        this.conn = conn;
    }

    public SimpleMeetingDAO(){
        uniqueMeetings = new HashSet<>();
        uniqueMeetings = getAllMeetingHashes();

    }


    private boolean openConnection(){
        conn = ConnectionFactory.getConnection();
        try{
            if(conn.isClosed())
                return false;
        }catch(SQLException e){
            return false;
        }
        return true;
    }

    private boolean closeConnection(){
       try{
           conn.close();
           return true;
       }catch(SQLException e){
           System.out.println("Error closing conection from connection factory");
           return false;
       }
    }

    @Override
    public void insertMeeting(SimpleMeeting m) {
        if(openConnection()) {
            try{
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO simple_meeting" +
                        "(s_date, e_date, day_of_week) VALUES (?::date,?::date,?);");
                stmt.setString(1,m.getStartDate());
                stmt.setString(2,m.getEndDate());
                stmt.setString(3,m.getDayOfWeek());
                int affectedRows = stmt.executeUpdate();
                if(affectedRows > 0)
                    System.out.println("Insert affected: " + affectedRows + " Rows");
                stmt.close();
                closeConnection();
            }catch(SQLException e){

            }

        }
    }

    @Override
    public int insertException(String date, String dow){
        if(openConnection()){
            try{
                int meetingIDForException=0;
                PreparedStatement meetingId = conn.prepareStatement("SELECT id from simple_meeting where " +
                        "?::date between s_date and e_date and day_of_week = ?;");
                meetingId.setString(1, date);
                meetingId.setString(2, dow);
                ResultSet rs = meetingId.executeQuery();

                while(rs.next()){
                    meetingIDForException = rs.getInt(1);
                }

                PreparedStatement stmt = conn.prepareStatement("INSERT INTO simple_meeting_exception" +
                        "(meeting_id,exception_date, is_cancelled) VALUES" +
                        "(?, ?::date, ?); ");
                stmt.setInt(1,meetingIDForException);
                stmt.setString(2,date);
                stmt.setBoolean(3,true);
                int affectedRow = stmt.executeUpdate();
                if(affectedRow > 0)
                    return affectedRow;
                else
                    return 0;

            }catch(SQLException e){
                e.printStackTrace();

            }
            closeConnection();
        }
        return 0;
    }

    @Override
    public int[] insertMeetingList(List<SimpleMeeting> meetings){

        int[] affectedRows = new int[meetings.size()];

        if(meetings == null)
            return affectedRows;

        if(openConnection()){
            try{
                PreparedStatement stmt = null;
                stmt = conn.prepareStatement("INSERT INTO simple_meeting" +
                        "(s_date, e_date, day_of_week) VALUES (?::date, ?::date, ?);");
                conn.setAutoCommit(false);
                for(SimpleMeeting m : meetings){
                    stmt.setString(1,m.getStartDate());
                    stmt.setString(2,m.getEndDate());
                    stmt.setString(3,m.getDayOfWeek());
                    stmt.addBatch();
                }
                if(stmt != null) {
                    affectedRows = stmt.executeBatch();
                    stmt.clearBatch();
                }
                conn.commit();

            }catch(SQLException e){
                e.printStackTrace();
            }

            closeConnection();
        }
        return affectedRows;
    }



    //@TODO: create better primary key for a meeting. meeting id isn't known to the user so
    //@TODO: having them ask for a meeting needs to be done via multiple variables or just the day of week.
    @Override
    public SimpleMeeting getMeeting(int id) {

        SimpleMeeting meeting = new SimpleMeeting();

        if(openConnection()) {
            try {

                PreparedStatement prepStatement;
                prepStatement = conn.prepareStatement("SELECT DISTINCT * FROM simple_meeting where id = ?");
                prepStatement.setInt(1, id);

                ResultSet rs = prepStatement.executeQuery();

                while (rs.next()) {
                    int m_id = rs.getInt(1);
                    String startDate = rs.getString(2);
                    String endDate = rs.getString(3);
                    String day = rs.getString(4);
                    meeting.setDayOfWeek(day);
                    meeting.setStartDate(startDate);
                    meeting.setEndDate(endDate);

                }
                //@TODO: Add error checking and ensure connection closes. Log if it doesn't close and retry
                closeConnection();
                return meeting;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return meeting;
    }


    //@TODO: Implement exceptions to meetings that get rescheduled. Will need another method or query to append.
    @Override
    public int getMeetingCount(String day){
        int totalCount=0;
        if(openConnection()) {
            try {
                PreparedStatement stmt = conn.prepareStatement("select count(*), simple_meeting.day_of_week from simple_meeting inner join calendar on\n" +
                        "  simple_meeting.day_of_week = calendar.day_of_week where cal_date between simple_meeting.s_date\n" +
                        "    and simple_meeting.e_date and simple_meeting.day_of_week LIKE ? and\n" +
                        "      cal_date NOT IN (Select cal_date from calendar inner join simple_meeting_exception on\n" +
                        "        exception_date = cal_date where day_of_week LIKE ?) group by simple_meeting.day_of_week ;");
                stmt.setString(1, day);
                stmt.setString(2, day);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    totalCount = rs.getInt(1);
                }
                closeConnection();
            } catch (SQLException e) {
                //Insert log info and print statement
            }
        }
        return totalCount;
    }



    //@TODO: Implement method which creates a new meeting row in the DB, It also updates the previous meeting's
    //@TODO: end date to the day you start the new meeting. We ensure we don't double count meeting days.
    @Override
    public int updateMeetingDay(String s_date, String e_date, String oDay, String nDay) {

        int updatedRow = 0;
        String originalEndDate = e_date;
        String originalDay = oDay;
        String newDay = nDay;

        if(openConnection()){
            try {
                PreparedStatement originalMeeting = conn.prepareStatement("SELECT id from simple_meeting " +
                        "where s_date = ? and e_date = ? and day_of_week = ?;");
                originalMeeting.setString(1, s_date);
                originalMeeting.setString(2, e_date);
                originalMeeting.setString(3, oDay);

                ResultSet rs = originalMeeting.executeQuery();
                int idToUpdate=0;
                while(rs.next()){
                    idToUpdate = rs.getInt(1);
                }

                originalMeeting = conn.prepareStatement("UPDATE simple_meeting SET " +
                        "e_date = current_date WHERE id = ?;");

                updatedRow += originalMeeting.executeUpdate();

                PreparedStatement updatedMeeting = conn.prepareStatement("INSERT INTO " +
                        "simple_meeting(s_date, e_date, day_of_week) VALUES(current_date + interval '1 day', ?::date, ?);");
                updatedMeeting.setString(1,originalEndDate);
                updatedMeeting.setString(2,newDay);
                updatedRow += updatedMeeting.executeUpdate();
                if(updatedRow > 0){
                    System.out.println("Updated " + updatedRow + " Rows of Simple_Meeting");
                }

                closeConnection();
            }catch(SQLException e){

            }
        }
        return updatedRow;
    }


    //@TODO: update values of a meeting date by closing off the end date as current_date, and inserting a new meeting.
    @Override
    public int updateMeetingDates(int id, String new_start, String new_end) {
        return 0;
    }

    //@TODO: Provide universal delete method with ways to access the delete other than meeting ID.
    @Override
    public int deleteMeeting(int id) {
        if(openConnection()){
            try{
                PreparedStatement stmt = conn.prepareStatement("DELETE from simple_meeting where id = ?;");
                stmt.setInt(1,id);
                int affectedRow = stmt.executeUpdate();
                if(affectedRow > 0)
                    return affectedRow;

            }catch(SQLException e){
                System.out.println("Error deleting Meeting, did not access the database");
            }
            closeConnection();
        }
        return 0;
    }

    //@TODO: Make the input validation greater for insertion into the database. Deal with multiple overlap meetings.
    public HashSet<String> getAllMeetingHashes() {
        HashSet<String> output = new HashSet<>();

        if (openConnection()) {
            try {

                PreparedStatement prepStatement;
                prepStatement = conn.prepareStatement("SELECT * FROM simple_meeting;");
                ResultSet rs = prepStatement.executeQuery();
                while (rs.next()) {
                    String startDate = rs.getString(2);
                    String endDate = rs.getString(3);
                    String day = rs.getString(4);
                    StringBuilder sb = new StringBuilder();
                    sb.append(startDate);
                    sb.append(endDate);
                    sb.append(day);
                    output.add(sb.toString());

                }
                //@TODO: Add error checking and ensure connection closes. Log if it doesn't close and retry
                closeConnection();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return output;
    }


    public void updateMeetingHashes(){
        this.uniqueMeetings = getAllMeetingHashes();
    }

    public int getTotalEntries(){
        updateMeetingHashes();
        return this.uniqueMeetings.size();
    }

    public boolean containsMeeting(SimpleMeeting m){
        boolean containsMeeting = false;
        StringBuilder currentMeeting = new StringBuilder();
        currentMeeting.append(m.getStartDate());
        currentMeeting.append(m.getEndDate());
        currentMeeting.append(m.getDayOfWeek());
        if(this.uniqueMeetings.contains(currentMeeting.toString())){
            containsMeeting = true;
        }
        return containsMeeting;
    }
}
