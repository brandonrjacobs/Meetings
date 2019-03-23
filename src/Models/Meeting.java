package Models;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Date;

public class Meeting {

    private static final String DATE_FORMAT = "yyyy-mm-dd";
    private static final AtomicInteger count = new AtomicInteger();

    private int id;
    private String meetingName;
    private String meetingDesc;
    private String startingDate;
    private String endingDate;
    private String startingTime;
    private String endingTime;
    private boolean isFullDayMeeting;
    private boolean isRecurring;
    private int createdByID;
    private String createdDate;

    //Recurring parameters for DAO
    private int dayOfWeek;
    private int weekOfMonth;
    private int dayOfMonth;
    private int monthOfYear;

    //Set the primaryKey Increment to the latest ID in the database. Retrieve upon obtaining the state of the DB.
    protected void resetIDIncrement(int newestID){
        count.set(newestID);
    }

    private void setPrimaryKeyID(){
        this.id = count.incrementAndGet();
    }

    public int getPrimaryKeyID(){
        return this.id;
    }

    public void setMeetingName(String name){
        this.meetingName = name;
    }

    public String getMeetingName(){
        return this.meetingName;
    }

    public void setMeetingDesc(String desc){
        this.meetingDesc = desc;
    }

    public String getMeetingDesc(){
        return this.meetingDesc;
    }

    public void setStartingDate(String date){
        this.startingDate = date;
    }

    public String getStartingDate(){
        return this.startingDate;
    }

    public void setEndingDate(String date){
        this.endingDate = date;
    }

    public String getEndingDate(){
        return this.endingDate;
    }

    public void setRecurring(boolean recur){ this.isRecurring = recur; }

    public boolean isRecurring(){ return this.isRecurring; }

    public void setFullDayMeeting(boolean isFullDay){
        this.isFullDayMeeting = isFullDay;
    }

    public boolean isFullDayMeeting(){
        return this.isFullDayMeeting;
    }

    public void setCreateID(int id){
        this.createdByID = id;
    }

    public int getCreatedByID(){
        return this.createdByID;
    }

    public void setCreatedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        this.createdDate = sdf.format(new Date());
    }

    public String getCreatedDate(){
        return this.createdDate;
    }

    //Fully created Meeting with all information
    public Meeting(String name, String desc, String sDate, String eDate, boolean isFullDay, int createID){
        //this.setPrimaryKeyID();
        this.setMeetingName(name);
        this.setMeetingDesc(desc);
        this.setStartingDate(sDate);
        this.setEndingDate(eDate);
        this.setFullDayMeeting(isFullDay);
        this.setCreateID(createID);
        this.setCreatedDate();
    }

    //Minimum required information for a Meeting to be scheduled (Meeting Name, Starting Date, Creator ID)
    public Meeting(String name, String sDate, int createID){
        //this.setPrimaryKeyID();
        this.setMeetingName(name);
        this.setStartingDate(sDate);
        this.setMeetingDesc(null);
        this.setEndingDate(null);
        this.setFullDayMeeting(false);
        this.setCreateID(createID);
        this.setCreatedDate();
    }

    public void getLengthOfMeeting(){

    }


}

