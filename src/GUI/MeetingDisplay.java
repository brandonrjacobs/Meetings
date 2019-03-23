package GUI;
import Models.SimpleMeeting;
import main.parser.CSVParser;

import javax.swing.*;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

public class MeetingDisplay {
    public static final String[] dayChoices = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
        "Sunday"};
    public static HashMap<String, String> days = new HashMap<>();
    public static void main(String[] args){
        days.put("Monday","Mon");
        days.put("Tuesday","Tue");
        days.put("Wednesday","Wed");
        days.put("Thursday","Thu");
        days.put("Friday","Fri");
        days.put("Saturday","Sat");
        days.put("Sunday","Sun");
        JFrame meetingInput = new JFrame("Find Meeting Occurances");

        boolean isAlive = true;

        while(isAlive){
           try {
               String filePath = JOptionPane.showInputDialog("Enter File Path");

               Reader input = new FileReader(filePath);
               List<SimpleMeeting> meetings = CSVParser.parseFile(input);

           }catch(Exception e){
               //logger.info("File Not Found");
               System.out.println("File Not Found");
               System.exit(1);
           }
        }





        String input = (String) JOptionPane.showInputDialog( meetingInput,"What day do you want to find meetings for?",
                "Meeting Date", JOptionPane.QUESTION_MESSAGE, null, dayChoices, dayChoices[0]);

        String queryVariable = days.get(input);




    }
}
