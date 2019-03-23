package Models;

public class SimpleMeeting {

    private String startDate;
    private String endDate;
    private String dayOfWeek;
    private int id;

    public void setStartDate(String date){ this.startDate = date; }

    public String getStartDate(){ return this.startDate; }

    public void setEndDate(String date){ this.endDate = date; }

    public String getEndDate(){ return this.endDate; }

    public void setDayOfWeek(String day){ this.dayOfWeek = day; }

    public String getDayOfWeek(){ return this.dayOfWeek; }

    public void setId(int id){ this.id = id; }

    public int getId(){ return this.id; }




    public SimpleMeeting(){

    }

    public SimpleMeeting(String start, String end, String day){
        setStartDate(start);
        setEndDate(end);
        setDayOfWeek(day);
    }

    public boolean isEqual(SimpleMeeting m){

       boolean  sameDay = (this.dayOfWeek == m.dayOfWeek);
       boolean sameStart = (this.startDate == m.getStartDate());
       boolean sameEnd = (this.endDate == m.getEndDate());

       if(sameDay && sameStart && sameEnd)
           return true;
       else
           return false;
    }
}
