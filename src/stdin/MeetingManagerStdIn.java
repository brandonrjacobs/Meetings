package stdin;
import DAO.SimpleMeetingDAO;
import Models.SimpleMeeting;
import main.parser.CSVParser;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

public class MeetingManagerStdIn {

    /* Main Entry point for the standard Input format of a .csv file of Meetings */
    public static void main(String[] args){
        //Logger logger = LoggerFactory.getLogger(MeetingManagerStdIn.class);
        try{
            //Initialization of variables
            SimpleMeetingDAO dao = new SimpleMeetingDAO();
            HashMap<String,String> days = new HashMap<>();
            days.put("Monday","Mon");
            days.put("Tuesday","Tue");
            days.put("Wednesday","Wed");
            days.put("Thursday","Thu");
            days.put("Friday","Fri");
            days.put("Saturday","Sat");
            days.put("Sunday","Sun");

            Scanner scan = new Scanner(System.in);

            Reader input = new FileReader("/raytheon/meetings.csv");
            List<SimpleMeeting> meetings;
            meetings = CSVParser.parseFile(input);


            System.out.println("Current Database size of " + meetings.size() + " Meetings");
            System.out.println("------------------------------------");
            System.out.println("------------------------------------");
            System.out.println("------------------------------------");

            int opt = -1;

            while(opt != 5) {
                System.out.println("Please Enter your option: ");
                System.out.println("(1) Find total meetings (2) Insert Meeting (3) Add Exception (4) load csv (5) Exit");

                //@TODO: Validate int input and not string characters
                opt = scan.nextInt();
                switch(opt) {

                    case 1:
                        System.out.println("What day do you want the meetings for? \n Day: ");
                        String day = scan.next();

                        String abbrev="";
                        if(days.containsKey(day))
                            abbrev = days.get(day);
                        else
                            System.out.println("Please Enter a day of the week.");

                        int totalMeetings = dao.getMeetingCount(abbrev);
                        System.out.println("Total Meetings for Day: " + day + " = " + totalMeetings);
                        continue;

                    case 2:
                        System.out.println("Enter new meeting Start Date: ");
                        String sDate = scan.next();
                        System.out.println("Enter new meeting End Date: ");
                        String eDate = scan.next();
                        System.out.println("Enter the day of the week: ");
                        String newDay = scan.next();
                        if(days.containsKey(newDay))
                            newDay = days.get(newDay);
                        SimpleMeeting m = new SimpleMeeting();
                        m.setStartDate(sDate);
                        m.setEndDate(eDate);
                        m.setDayOfWeek(newDay);
                        System.out.println("Database size is: " + dao.getTotalEntries());
                        dao.insertMeeting(m);
                        System.out.println("Database size is now: " + dao.getTotalEntries() + " after Entry");

                    case 3:
                        System.out.println("What day would you like to cancel your Meeting?");
                        System.out.println("Enter Date: ");
                        String dateCancel = scan.next();
                        System.out.println("What is your meeting day? ");
                        System.out.println("Enter Day of Week: ");
                        String dow = scan.next();
                        dow = days.get(dow);
                        System.out.println("Meeting Occurances before cancelling: " + dao.getMeetingCount("Mon"));
                        dao.insertException(dateCancel, dow);
                        System.out.println("Meeting occurances after cancelling: " + dao.getMeetingCount("Mon"));

                    case 4:

                    case 5:

                    default:

                }
            }
        }catch(Exception e){
            //logger.info("File Not Found from the reader");
            System.out.println("File Not Found");
        }


    }

}
