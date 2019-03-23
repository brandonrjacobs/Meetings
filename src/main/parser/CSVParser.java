package main.parser;

import Models.SimpleMeeting;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    /*@TODO: This method does not follow loose coupling and relies on the design of SimpleMethod. Decouple the design
     *
     */
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
}
