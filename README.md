# Meetings

## Design

This project is a prototype for a reusable Interface to create, read, update, and delete meeting objects. The idea
behind the structure was to enable fast prototyping via command line arguments.

The main interface is the 

```java
public class SimpleMeetingDAO()
```

which implements

```java
public interface ISimpleMeetingDAO()
```


Data is modeled and for input and output via the class
```java
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
 
```

Further development and use cases will be handled via the class
```java
public class Meeting()
```
which holds a more robust and thurough implementation of a meeting object. 

### File Parsing
One of the use cases is for a user to load meetings via a .csv file. This use case makes use of our CSVParser and
the validate methods in main.parser. 
```java
    public static List<SimpleMeeting> parseFile(Reader r) throws Exception{
        List<SimpleMeeting> meetings = new ArrayList<>();
        //Logger logger = LoggerFactory.getLogger(SimpleMeetingValidator.class);
        SimpleMeetingValidator validator = new SimpleMeetingValidator();
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(r);
        SimpleMeeting meeting = new SimpleMeeting();
        for(CSVRecord record : records){

            String startDate = record.get(0);
            String endDate = record.get(1);
            String dayOfWeek = record.get(2);

            meeting.setStartDate(startDate);
            meeting.setEndDate(endDate);
            meeting.setDayOfWeek(dayOfWeek);

            if(!validator.validateSimpleMeeting(meeting)){

                System.out.println("Non valid format");

                //Meeting is not a valid format and we will log the error and skip the meeting record
                String logOutput = "Invalid Record Format for Record: " + record.getRecordNumber() +", Did not meet \"" +
                        "validation Criteria and will be skipped";

                //logger.info(logOutput);
            }
            else{
                //Is a valid meeting so store it in the list of SimpleMeetings
                meetings.add(meeting);
                meeting = new SimpleMeeting();
            }
        }

        return meetings;
    }
```

### Data Access Objects (Meat of the Interface)
The Data Access Object SimpleMeetingDAO is the heart of the application. This is where we are able to Create, Read, Update, 
and Delete meetings or exceptions to meetings. This is how we also query the occurances of any meeting for a given period of
time. There are mulitple TODO's for future version's. I will document those at the bottom of ths file. Inside our DAO is the 
fundamental SQL query needed to find the right number of meetings. The following query will return meetings between a given 
date on a specific day of week, but will not account for any cancelled meetings.

```sql
select count(*), simple_meeting.day_of_week from simple_meeting inner join calendar on
  simple_meeting.day_of_week = calendar.day_of_week where cal_date between simple_meeting.s_date
    and simple_meeting.e_date and simple_meeting.day_of_week LIKE 'Sun' and
      cal_date NOT IN (Select cal_date from calendar inner join simple_meeting_exception on
        exception_date = cal_date where day_of_week LIKE 'Sun') group by simple_meeting.day_of_week ;
```
As you can see, by having a calendar table that holds entries for every day of the year we can easily access date values
and simplify the query complexity. It is possible to not have a calendar table but I made the decision to use a separate table 
and query against in order to make the query readable and therefore easier to debug. 

Another reason I decided to keep the calendar table was that we can assume that every day past the current_date can be removed
from the calendar. But only if we keep a current  running estimate in another table of meeting_counts. Whatever the method 
is, I figured we would only theoretically have to keep 3-5 years of days in a table meaning the rows would equal 365 x 5. 

### SimpleMeetingDAO Methods
When looking at creating a data access object I tried to make it straight forward and as encapsulated as I could.
By utilizing a ConnectionFactory and writing methods such as ```java public boolean openConnection()``` I was able to 
compartmentalize logic and reduce repeated code. 

The method that actually returns the meeting count is
```java
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
```
Utilizing PreparedStatements we are able to pass in the day we wish to count meetings for. Future implementations can be done
to count meetings based on a date range which would just return all meetings  between the two dates and group them by the day.

Something I did not implement at first but thought would come in useful was
```java
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
```
This method updates and populates the HashSet stored in our SimpleMeetingDAO. I chose to do this to check if our unique meeting
has been stored in the Database already. Instead of using loops to compare to every meeting object pulled from the database
I chose to store a hash of all three values in the Set. By generating the hash of the object to compare I check if HashSet 
contains the new meeting. If it does, we discard and do not insert it into the database. This could be done on the Database Side 
by putting constraints on the table but because I was playing around with how I wanted to further implement the table i left the 
validation out of the DB.

### Input Limitation and Data Validation
The program as is can only handle certain input and the database query will not pull the correct information if we duplicate
or overwrite meeting date intervals. The following are the Input Restrictions currently followed:

#### Meetings must be a unique tuple of (Start Date, End Date, Day of Week)
Any row in the database that has a start date inside the interval of (Start Date, End Date) and have the same Day of the Week
will not be allowed to be stored. This is because as of right now we do not differentiate between meeting types, we assume 
that if we store that meeting, we would be double counting the dates.

#### Design Consideration
A simple design addition would be to assign a name to the meeting so that we can have two intervals that overlap and on the 
same day but will not get counted if their names are different. There are many other ways to denormalize the tables and create
unique keys that will allow us to pull the correct info. Given more time we can come up with a thorough design. 

## Future Consideration & TODO's
### Decouple and Abstract the Utility Functions
Currently the CSVParser and Validator are specific to the SimpleMeeting class structure so any changes will cascade into
chose classes and their implementation. Future revisions will focus on creating generic yet versatile implementation that
can handle changes in object types and class implementations. 

### Implement Meeting Object and MeetingDAO
Meeting and MeetingDAO were the goal for this project. SimpleMeeting handles dates and a Day of the week, while Meeting 
is for very specific meetings with unique time intervals between meetings. The idea being eventually we will want to 
generate meetings that happen every two weeks, the third Wednesday of each month, quarterly, and so on. If so, we need a model
that can handle this. This is why there are more tables created to start that design. The queries were not yet constructed but 
the initial planning for them was. 

### Clean up and finalize the RESTful API
This is a not so clean implementation of an interface but in the future should be cleaned up and abstract a lot of the
database heavy lifting in order to be successful with use in a front end web framework. 

### Standardize Exception Handling and Implement a Logger 
Playing around with the different frameworks and possible Exceptions I need to implement a standard Exception Handling system.
This means that I need to produce the same output based on the same exceptions and log them to a central file to debug. 
What this project lacks is a formal and standard exception standard. Implementing a logger that outputs everything to a file
will be tremendesly helpful when dealing with database queries and insertions/deletions in the future. 


